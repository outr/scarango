package com.outr.arango

import com.outr.arango.rest._
import io.circe.generic.auto._
import io.circe.{Decoder, Encoder, Json}
import io.youi.http.{HttpResponse, Method}

import scala.concurrent.Future

class ArangoDB(session: ArangoSession, db: String) {
  protected def restful[Request, Response](name: String,
                                           request: Request,
                                           params: Map[String, String] = Map.empty,
                                           errorHandler: HttpResponse => Response = session.instance.defaultErrorHandler[Response])
                                          (implicit encoder: Encoder[Request], decoder: Decoder[Response]): Future[Response] = {
    session.instance.restful[Request, Response](s"/_db/$db/_api/$name", request, Some(session.token), params, errorHandler)
  }

  protected def call[Response](name: String,
                               method: Method,
                               params: Map[String, String] = Map.empty,
                               errorHandler: HttpResponse => Response = session.instance.defaultErrorHandler[Response])
                              (implicit decoder: Decoder[Response]): Future[Response] = {
    session.instance.call[Response](s"/_db/$db/_api/$name", method, Some(session.token), params, errorHandler)
  }

  def collections(excludeSystem: Boolean = true): Future[Collections] = {
    call[Collections](s"collection", Method.Get, params = Map(
      "excludeSystem" -> excludeSystem.toString
    ))
  }

  def createCollection(name: String,
                       journalSize: Option[Long] = None,
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
      name = name,
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
    restful[CreateCollectionRequest, CreateCollectionResponse]("collection", request)
  }

  def loadCollection(name: String, count: Boolean = true): Future[CollectionLoad] = {
    call[CollectionLoad](s"collection/$name/load", Method.Put, params = Map(
      "count" -> count.toString
    ))
  }

  def unloadCollection(name: String): Future[CollectionLoad] = {
    call[CollectionLoad](s"collection/$name/unload", Method.Put)
  }

  def collectionInformation(name: String): Future[CollectionInformation] = {
    call[CollectionInformation](s"collection/$name", Method.Get)
  }

  def collectionProperties(name: String): Future[CollectionProperties] = {
    call[CollectionProperties](s"collection/$name/properties", Method.Get)
  }

  def collectionCount(name: String): Future[CollectionCount] = {
    call[CollectionCount](s"collection/$name/count", Method.Get)
  }

  def collectionRevision(name: String): Future[CollectionRevision] = {
    call[CollectionRevision](s"collection/$name/revision", Method.Get)
  }

  def truncateCollection(name: String): Future[TruncateCollectionResponse] = {
    call[TruncateCollectionResponse](s"collection/$name/truncate", Method.Put)
  }

  def dropCollection(name: String, isSystem: Boolean = false): Future[DropCollectionResponse] = {
    call[DropCollectionResponse](s"collection/$name", Method.Delete, params = Map(
      "isSystem" -> isSystem.toString
    ))
  }

  def cursor(query: String, count: Boolean, batchSize: Int): Future[QueryResponse] = {
    restful[QueryRequest, QueryResponse]("cursor", QueryRequest(query, count, batchSize))
  }

  def document[T](collection: String, documentHandle: String)
                 (implicit decoder: Decoder[T]): Future[T] = {
    call[T](s"document/$collection/$documentHandle", Method.Get)
  }

  def createDocument[T](collection: String,
                        document: T,
                        waitForSync: Boolean = false,
                        returnNew: Boolean = false,
                        silent: Boolean = false)
                       (implicit encoder: Encoder[T], decoder: Decoder[CreateDocument[T]]): Future[CreateDocument[T]] = {
    restful[T, CreateDocument[T]](s"document/$collection", document, params = Map(
      "waitForSync" -> waitForSync.toString,
      "returnNew" -> returnNew.toString,
      "silent" -> silent.toString
    ))
  }
}