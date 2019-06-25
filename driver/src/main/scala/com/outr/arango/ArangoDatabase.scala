package com.outr.arango

import com.outr.arango.api.{APICollection, APIDatabase, APIDatabaseUser, APIQuery, APIView}
import com.outr.arango.JsonImplicits._
import com.outr.arango.api.model.PostApiQueryProperties
import com.outr.arango.model.ArangoResponse
import io.circe.Json
import io.youi.client.HttpClient
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
    future.map(JsonUtil.fromJson[ArangoResponse[List[String]]](_))
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
    .map(JsonUtil.fromJson[ValidationResult](_))
    .recover {
      case exc: ArangoException => JsonUtil.fromJsonString[ValidationResult](exc.response.content.get.asString)
    }

  def query(query: Query): QueryBuilder[Json] = QueryBuilder[Json](client, query.fixed(), identity)

  def drop()(implicit ec: ExecutionContext): Future[ArangoResponse[Boolean]] = db.api.system.drop(name)
}