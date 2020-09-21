package com.outr.arango

import com.outr.arango.api.{APICollection, APIDatabase, APIDatabaseUser, APIQuery, APITransaction, APIView, APIWalTail, WALOperations}
import com.outr.arango.JsonImplicits._
import com.outr.arango.api.model.{PostAPITransaction, PostApiQueryProperties}
import com.outr.arango.model.ArangoResponse
import com.outr.arango.transaction.Transaction
import io.circe.Json
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import profig.JsonUtil

import scala.concurrent.{ExecutionContext, Future}

class ArangoDatabase(db: ArangoDB, protected val client: HttpClient, val name: String) {
  def create()(implicit ec: ExecutionContext): Future[ArangoResponse[Boolean]] = db.api.system.create(name)

  def list(accessibleOnly: Boolean = true)(implicit ec: ExecutionContext): Future[ArangoResponse[List[String]]] = {
    val future = if (accessibleOnly) {
      APIDatabaseUser.get(client)
    } else {
      APIDatabase.get(client)
    }
    future.map(json => JsonUtil.fromJson[ArangoResponse[List[String]]](json))
  }

  def collection(name: String): ArangoCollection = {
    new ArangoCollection(client, this.name, name)
  }

  def collections(excludeSystem: Boolean = true)
                 (implicit ec: ExecutionContext): Future[List[CollectionDetail]] = {
    APICollection.get(client, Some(excludeSystem)).map { json =>
      JsonUtil.fromJson[List[CollectionDetail]]((json \ "result").get)
    }
  }

  def views()(implicit ec: ExecutionContext): Future[List[ViewDetail]] = {
    APIView.get(client).map(JsonUtil.fromJson[ArangoResponse[List[ViewDetail]]](_).result.getOrElse(Nil))
  }

  def searchView(name: String): ArangoView = {
    new ArangoView(client, this.name, name, "arangosearch")
  }

  def validate(query: String)(implicit ec: ExecutionContext): Future[ValidationResult] = APIQuery
    .post(client, PostApiQueryProperties(query))
    .map(json => JsonUtil.fromJson[ValidationResult](json))
    .recover {
      case exc: ArangoException => JsonUtil.fromJsonString[ValidationResult](exc.response.content.get.asString)
    }

  def transaction(queries: List[Query],
                  writeCollections: List[String] = Nil,
                  readCollections: List[String] = Nil,
                  exclusiveCollections: List[String] = Nil,
                  waitForSync: Boolean = false,
                  lockTimeout: Option[Long] = None,
                  maxTransactionSize: Option[Long] = None)
                 (implicit ec: ExecutionContext): Future[ArangoResponse[Vector[Json]]] = {
    assert(queries.nonEmpty, "Cannot create a transaction with no queries!")
    val queryString = queries.map(_.fix()).map { q =>
      val params = q.bindVars.spaces2
      s"""results.push(db._query("${q.value}", $params));"""
    }.mkString("\n  ")
    val function =
      s"""function() {
         |  var db = require("org/arangodb").db;
         |  var results = [];
         |  $queryString
         |  return results;
         |}""".stripMargin
    APITransaction.post(client, PostAPITransaction(
      collections = Json.obj(
        "write" -> Json.arr(writeCollections.map(Json.fromString): _*),
        "read" -> Json.arr(readCollections.map(Json.fromString): _*),
        "exclusive" -> Json.arr(exclusiveCollections.map(Json.fromString): _*)
      ),
      action = Some(function),
      lockTimeout = lockTimeout,
      maxTransactionSize = maxTransactionSize,
      params = None,
      waitForSync = Some(waitForSync)
    )).map(json => JsonUtil.fromJson[ArangoResponse[Vector[Json]]](json))
  }

  def transactionCreate(writeCollections: List[String] = Nil,
                        readCollections: List[String] = Nil,
                        exclusiveCollections: List[String] = Nil,
                        waitForSync: Boolean = false,
                        allowImplicit: Boolean = false,
                        maxTransactionSize: Long = -1L)
                       (implicit ec: ExecutionContext): Future[Transaction] = {
    var request = Json.obj(
      "collections" -> Json.obj(
        "write" -> Json.arr(writeCollections.map(Json.fromString): _*),
        "read" -> Json.arr(readCollections.map(Json.fromString): _*),
        "exclusive" -> Json.arr(exclusiveCollections.map(Json.fromString): _*)
      ),
      "waitForSync" -> Json.fromBoolean(waitForSync),
      "allowImplicit" -> Json.fromBoolean(allowImplicit)
    )
    if (maxTransactionSize != -1L) {
      request = request.deepMerge(Json.obj("maxTransactionSize" -> Json.fromLong(maxTransactionSize)))
    }
    client
      .method(HttpMethod.Post)
      .path(path"/_api/transaction/begin", append = true)
      .restful[Json, ArangoResponse[Transaction]](request)
      .map(_.value)
  }

  def transactionStatus(id: String)(implicit ec: ExecutionContext): Future[Transaction] = {
    client
      .method(HttpMethod.Get)
      .path(Path.parse(s"/_api/transaction/$id"), append = true)
      .call[ArangoResponse[Transaction]]
      .map(_.value)
  }

  def transactionCommit(id: String)(implicit ec: ExecutionContext): Future[Transaction] = {
    client
      .method(HttpMethod.Put)
      .path(Path.parse(s"/_api/transaction/$id"), append = true)
      .call[ArangoResponse[Transaction]]
      .map(_.value)
  }

  def transactionAbort(id: String)(implicit ec: ExecutionContext): Future[Transaction] = {
    client
      .method(HttpMethod.Delete)
      .path(Path.parse(s"/_api/transaction/$id"), append = true)
      .call[ArangoResponse[Transaction]]
      .map(_.value)
  }

  def transactionList()(implicit ec: ExecutionContext): Future[List[Transaction]] = {
    client
      .method(HttpMethod.Get)
      .path(path"/_api/transaction", append = true)
      .call[Json]
      .map { json =>
        JsonUtil.fromJson[List[Transaction]]((json \\ "transactions").head)
      }
  }

  def query(query: Query, transactionId: Option[String] = None): QueryBuilder[Json] = {
    val c = transactionId match {
      case Some(tId) => client.header("x-arango-trx-id", tId)
      case None => client
    }
    QueryBuilder[Json](c, query.fix(), identity)
  }

  lazy val wal: ArangoWriteAheadLog = new ArangoWriteAheadLog(client)

  def drop()(implicit ec: ExecutionContext): Future[ArangoResponse[Boolean]] = db.api.system.drop(name)
}