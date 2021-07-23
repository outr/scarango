package com.outr.arango

import com.outr.arango.api.{APICollection, APIDatabase, APIDatabaseUser, APIQuery, APITransaction, APIView, APIWalTail, WALOperations}
import com.outr.arango.api.model.{PostAPITransaction, PostApiQueryProperties}
import com.outr.arango.model.ArangoResponse
import com.outr.arango.transaction.Transaction
import fabric._
import fabric.parse.Json
import fabric.rw.{Asable, ReaderWriter}
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.youi.net.Path

import scala.concurrent.{ExecutionContext, Future}

class ArangoDatabase(db: ArangoDB, protected val client: HttpClient, val name: String) {
  def create()(implicit ec: ExecutionContext): Future[Boolean] = db.api.system.create(name)

  def list(accessibleOnly: Boolean = true)(implicit ec: ExecutionContext): Future[List[String]] = {
    val future = if (accessibleOnly) {
      APIDatabaseUser.get(client)
    } else {
      APIDatabase.get(client)
    }
//    implicit val listStringRW: ReaderWriter[List[String]] =
    future.map(_.as[ArangoResponse].value[List[String]])
  }

  def collection(name: String): ArangoCollection = {
    new ArangoCollection(client, this.name, name)
  }

  def collections(excludeSystem: Boolean = true)
                 (implicit ec: ExecutionContext): Future[List[CollectionDetail]] = {
    APICollection.get(client, Some(excludeSystem)).map { json =>
      json("result").as[List[CollectionDetail]]
    }
  }

  def views()(implicit ec: ExecutionContext): Future[List[ViewDetail]] = {
    APIView.get(client)
      .map(_.as[ArangoResponse].value[List[ViewDetail]])
  }

  def searchView(name: String): ArangoView = {
    new ArangoView(client, this.name, name, "arangosearch")
  }

  def validate(query: String)(implicit ec: ExecutionContext): Future[ValidationResult] = APIQuery
    .post(client, PostApiQueryProperties(query))
    .map(_.as[ValidationResult])
    .recover {
      case exc: ArangoException => Json.parse(exc.response.content.get.asString).as[ValidationResult]
    }

  def transaction(queries: List[Query],
                  writeCollections: List[String] = Nil,
                  readCollections: List[String] = Nil,
                  exclusiveCollections: List[String] = Nil,
                  waitForSync: Boolean = false,
                  lockTimeout: Option[Long] = None,
                  maxTransactionSize: Option[Long] = None)
                 (implicit ec: ExecutionContext): Future[List[Value]] = {
    assert(queries.nonEmpty, "Cannot create a transaction with no queries!")
    val queryString = queries.map(_.fix()).map { q =>
      val params = Json.format(q.bindVars)
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
      collections = obj(
        "write" -> arr(writeCollections.map(str): _*),
        "read" -> arr(readCollections.map(str): _*),
        "exclusive" -> arr(exclusiveCollections.map(str): _*)
      ),
      action = Some(function),
      lockTimeout = lockTimeout,
      maxTransactionSize = maxTransactionSize,
      params = None,
      waitForSync = Some(waitForSync)
    )).map(_.as[ArangoResponse].value[List[Value]])
  }

  def transactionCreate(writeCollections: List[String] = Nil,
                        readCollections: List[String] = Nil,
                        exclusiveCollections: List[String] = Nil,
                        waitForSync: Boolean = false,
                        allowImplicit: Boolean = false,
                        maxTransactionSize: Long = -1L)
                       (implicit ec: ExecutionContext): Future[Transaction] = {
    var request: Value = obj(
      "collections" -> obj(
        "write" -> arr(writeCollections.map(str): _*),
        "read" -> arr(readCollections.map(str): _*),
        "exclusive" -> arr(exclusiveCollections.map(str): _*)
      ),
      "waitForSync" -> bool(waitForSync),
      "allowImplicit" -> bool(allowImplicit)
    )
    if (maxTransactionSize != -1L) {
      request = request.merge(obj("maxTransactionSize" -> num(maxTransactionSize.toDouble)))
    }
    client
      .method(HttpMethod.Post)
      .path(path"/_api/transaction/begin", append = true)
      .restful[Value, ArangoResponse](request)
      .map(_.value[Transaction])
  }

  def transactionStatus(id: String)(implicit ec: ExecutionContext): Future[Transaction] = {
    client
      .method(HttpMethod.Get)
      .path(Path.parse(s"/_api/transaction/$id"), append = true)
      .call[ArangoResponse]
      .map(_.value[Transaction])
  }

  def transactionCommit(id: String)(implicit ec: ExecutionContext): Future[Transaction] = {
    client
      .method(HttpMethod.Put)
      .path(Path.parse(s"/_api/transaction/$id"), append = true)
      .call[ArangoResponse]
      .map(_.value[Transaction])
  }

  def transactionAbort(id: String)(implicit ec: ExecutionContext): Future[Transaction] = {
    client
      .method(HttpMethod.Delete)
      .path(Path.parse(s"/_api/transaction/$id"), append = true)
      .call[ArangoResponse]
      .map(_.value[Transaction])
  }

  def transactionList()(implicit ec: ExecutionContext): Future[List[Transaction]] = {
    client
      .method(HttpMethod.Get)
      .path(path"/_api/transaction", append = true)
      .call[Value]
      .map { json =>
        json("transactions").as[List[Transaction]]
      }
  }

  def query(query: Query, transactionId: Option[String] = None): QueryBuilder[Value] = {
    val c = transactionId match {
      case Some(tId) => client.header("x-arango-trx-id", tId)
      case None => client
    }
    QueryBuilder[Value](c, query.fix(), identity)
  }

  lazy val wal: ArangoWriteAheadLog = new ArangoWriteAheadLog(client)

  def drop()(implicit ec: ExecutionContext): Future[Boolean] = db.api.system.drop(name)
}