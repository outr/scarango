package com.outr.arango

import com.outr.arango.rest._
import io.circe.generic.auto._
import io.circe.{Decoder, Encoder}
import io.youi.http.{HttpResponse, Method}

import scala.concurrent.Future

class ArangoCollection(val db: ArangoDB, val collection: String) {
  lazy val document: ArangoDocument = new ArangoDocument(this)

  protected[arango] def restful[Request, Response](name: Option[String],
                                                   request: Request,
                                                   params: Map[String, String] = Map.empty,
                                                   errorHandler: HttpResponse => Response = db.session.instance.defaultErrorHandler[Response])
                                                  (implicit encoder: Encoder[Request], decoder: Decoder[Response]): Future[Response] = {
    val path = name match {
      case Some(n) if n.isEmpty => s"collection/$collection"
      case Some(n) => s"collection/$collection/$n"
      case None => "collection"
    }
    db.restful[Request, Response](path, request, params, errorHandler)
  }

  protected[arango] def call[Response](name: Option[String],
                                       method: Method,
                                       params: Map[String, String] = Map.empty,
                                       errorHandler: HttpResponse => Response = db.session.instance.defaultErrorHandler[Response])
                                      (implicit decoder: Decoder[Response]): Future[Response] = {
    val path = name match {
      case Some(n) => s"collection/$collection/$n"
      case None => "collection"
    }
    db.call[Response](path, method, params, errorHandler)
  }

  def list(excludeSystem: Boolean = true): Future[Collections] = {
    call[Collections](None, Method.Get, params = Map(
      "excludeSystem" -> excludeSystem.toString
    ))
  }

  def create(journalSize: Option[Long] = None,
             replicationFactor: Int = 1,
             keyOptions: KeyOptions = KeyOptions(),
             waitForSync: Boolean = false,
             doCompact: Boolean = true,
             isVolatile: Boolean = false,
             shardKeys: Option[Array[String]] = None,
             numberOfShards: Int = 1,
             isSystem: Boolean = false,
             `type`: Int = 2,
             indexBuckets: Int = 16): Future[CreateCollectionResponse] = {
    val request = CreateCollectionRequest(
      name = collection,
      journalSize = journalSize,
      replicationFactor = replicationFactor,
      keyOptions = keyOptions,
      waitForSync = waitForSync,
      doCompact = doCompact,
      isVolatile = isVolatile,
      shardKeys = shardKeys,
      numberOfShards = numberOfShards,
      isSystem = isSystem,
      `type` = `type`,
      indexBuckets = indexBuckets
    )
    restful[CreateCollectionRequest, CreateCollectionResponse](None, request)
  }

  def load(count: Boolean = true): Future[CollectionLoad] = {
    call[CollectionLoad](Some("load"), Method.Put, params = Map(
      "count" -> count.toString
    ))
  }

  def unload(): Future[CollectionLoad] = {
    call[CollectionLoad](Some("unload"), Method.Put)
  }

  def information(): Future[CollectionInformation] = {
    call[CollectionInformation](Some(""), Method.Get)
  }

  def properties(): Future[CollectionProperties] = {
    call[CollectionProperties](Some("properties"), Method.Get)
  }

  def count(): Future[CollectionCount] = {
    call[CollectionCount](Some("count"), Method.Get)
  }

  def revision(): Future[CollectionRevision] = {
    call[CollectionRevision](Some("revision"), Method.Get)
  }

  def truncate(): Future[TruncateCollectionResponse] = {
    call[TruncateCollectionResponse](Some("truncate"), Method.Put)
  }

  def drop(isSystem: Boolean = false): Future[DropCollectionResponse] = {
    call[DropCollectionResponse](Some(""), Method.Delete, params = Map(
      "isSystem" -> isSystem.toString
    ))
  }
}

object ArangoCollection {
  val Document: Int = 2
  val Edges: Int = 3
}