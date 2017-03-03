package com.outr.arango

import io.circe.{Decoder, Encoder, Json}
import io.circe.generic.auto._
import io.youi.client.HttpClient
import io.youi.http.{Headers, HttpResponse, Method}
import io.youi.net.URL

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ArangoDB(baseURL: String) {
  private var disposed = false
  private val client = new HttpClient

  protected def url(path: String): URL = URL(s"$baseURL$path")

  protected[arango] def defaultErrorHandler[Response]: HttpResponse => Response = (response: HttpResponse) => {
    throw new RuntimeException(s"Error from server: ${response.status} with content: ${response.content}")
  }

  protected[arango] def restful[Request, Response](path: String,
                                                   request: Request,
                                                   token: Option[String],
                                                   errorHandler: HttpResponse => Response = defaultErrorHandler[Response])
                                                  (implicit encoder: Encoder[Request], decoder: Decoder[Response]): Future[Response] = {
    val headers = token.map(t =>Headers.empty.withHeader(Headers.Request.Authorization(s"bearer $t"))).getOrElse(Headers.empty)
    client.restful[Request, Response](url(path), request, headers, errorHandler)
  }

  protected[arango] def call[Response](path: String,
                                       method: Method,
                                       token: Option[String],
                                       errorHandler: HttpResponse => Response = defaultErrorHandler[Response])
                                      (implicit decoder: Decoder[Response]): Future[Response] = {
    val headers = token.map(t =>Headers.empty.withHeader(Headers.Request.Authorization(s"bearer $t"))).getOrElse(Headers.empty)
    client.call[Response](url(path), method, headers, errorHandler)
  }

  def auth(username: String, password: String): Future[ArangoSession] = {
    restful[AuthenticationRequest, AuthenticationResponse]("/_open/auth", AuthenticationRequest(username, password), None).map { response =>
      new ArangoSession(this, response.jwt)
    }
  }

  case class AuthenticationRequest(username: String, password: String)

  case class AuthenticationResponse(jwt: String, must_change_password: Boolean)

  def isDisposed: Boolean = disposed

  def dispose(): Unit = {
    client.dispose()
    disposed = true
  }
}

class ArangoSession(val server: ArangoDB, val token: String) {
  protected def restful[Request, Response](name: String,
                                           request: Request,
                                           errorHandler: HttpResponse => Response = server.defaultErrorHandler[Response])
                                          (implicit encoder: Encoder[Request], decoder: Decoder[Response]): Future[Response] = {
    server.restful[Request, Response](s"/_api/$name", request, Some(token), errorHandler)
  }

  protected def call[Response](name: String,
                               method: Method,
                               errorHandler: HttpResponse => Response = server.defaultErrorHandler[Response])
                              (implicit decoder: Decoder[Response]): Future[Response] = {
    server.call[Response](s"/_api/$name", method, Some(token), errorHandler)
  }

  def db(name: String): ArangoDBSession = new ArangoDBSession(this, name)

  def parse(query: String): Future[ParseResult] = {
    restful[ParseRequest, ParseResult]("query", ParseRequest(query), (response) => {
      ParseResult(error = true, code = response.status.code, parsed = false, collections = Nil, bindVars = Nil, ast = Nil)
    })
  }

  case class ParseRequest(query: String)

  case class ParseResult(error: Boolean,
                         code: Int,
                         parsed: Boolean,
                         collections: List[String],
                         bindVars: List[String],
                         ast: List[ParsedAST])

  case class ParsedAST(`type`: String,
                       name: Option[String],
                       id: Option[Int],
                       subNodes: Option[List[ParsedAST]])
}

object ArangoSession {
  def default: Future[ArangoSession] = {
    val url = Option(System.getenv("ARANGO_URL")).getOrElse("http://localhost:8529")
    val username = Option(System.getenv("ARANGO_USERNAME")).getOrElse("root")
    val password = Option(System.getenv("ARANGO_PASSWORD")).getOrElse("root")
    val instance = new ArangoDB(url)
    instance.auth(username, password)
  }
}

class ArangoDBSession(session: ArangoSession, db: String) {
  protected def restful[Request, Response](name: String,
                                           request: Request,
                                           errorHandler: HttpResponse => Response = session.server.defaultErrorHandler[Response])
                                          (implicit encoder: Encoder[Request], decoder: Decoder[Response]): Future[Response] = {
    session.server.restful[Request, Response](s"/_db/$db/_api/$name", request, Some(session.token), errorHandler)
  }

  protected def call[Response](name: String,
                               method: Method,
                               errorHandler: HttpResponse => Response = session.server.defaultErrorHandler[Response])
                              (implicit decoder: Decoder[Response]): Future[Response] = {
    session.server.call[Response](s"/_db/$db/_api/$name", method, Some(session.token), errorHandler)
  }

  def collections(excludeSystem: Boolean = true): Future[Collections] = {
    call[Collections](s"collection?excludeSystem=$excludeSystem", Method.Get)
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
    call[CollectionLoad](s"collection/$name/load?count=$count", Method.Put)
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
    call[DropCollectionResponse](s"collection/$name?isSystem=$isSystem", Method.Delete)
  }

  def cursor(query: String, count: Boolean, batchSize: Int): Future[QueryResponse] = {
    restful[QueryRequest, QueryResponse]("cursor", QueryRequest(query, count, batchSize))
  }

  case class CreateCollectionRequest(name: String,
                                     journalSize: Option[Long],
                                     replicationFactor: Int,
                                     keyOptions: KeyOptions,
                                     waitForSync: Boolean,
                                     doCompact: Boolean,
                                     isVolatile: Boolean,
                                     shardKeys: Option[Array[String]],
                                     numberOfShards: Int,
                                     isSystem: Boolean,
                                     `type`: Int,
                                     indexBuckets: Int)

  case class KeyOptions(allowUserKeys: Option[Boolean] = None,
                        `type`: Option[String] = None,
                        increment: Option[Int] = None,
                        offset: Option[Int] = None)

  case class Collections(result: List[Collection])

  case class Collection(id: String,
                        name: String,
                        isSystem: Boolean,
                        status: Int,
                        `type`: Int)

  case class CreateCollectionResponse(id: String,
                                      name: String,
                                      waitForSync: Boolean,
                                      isVolatile: Boolean,
                                      isSystem: Boolean,
                                      status: Int,
                                      `type`: Int,
                                      error: Boolean,
                                      code: Int)

  case class CollectionLoad(id: String,
                            name: String,
                            count: Option[Int],
                            status: Int,
                            `type`: Int,
                            isSystem: Boolean)

  case class CollectionInformation(id: String,
                                   name: String,
                                   status: Int,
                                   `type`: Int,
                                   isSystem: Boolean)

  case class CollectionProperties(waitForSync: Boolean,
                                  doCompact: Boolean,
                                  journalSize: Int,
                                  keyOptions: KeyOptions,
                                  isVolatile: Boolean,
                                  numberOfShards: Option[Int],
                                  shardKeys: Option[List[String]],
                                  replicationFactor: Option[Int])

  case class CollectionCount(id: String,
                             name: String,
                             isSystem: Boolean,
                             doCompact: Boolean,
                             isVolatile: Boolean,
                             journalSize: Long,
                             keyOptions: KeyOptions,
                             waitForSync: Boolean,
                             indexBuckets: Int,
                             count: Int,
                             status: Int,
                             `type`: Int,
                             error: Boolean,
                             code: Int)

  case class CollectionRevision(id: String,
                                name: String,
                                isSystem: Boolean,
                                status: Int,
                                `type`: Int,
                                revision: String,
                                error: Boolean,
                                code: Int)

  case class TruncateCollectionResponse(id: String,
                                        name: String,
                                        isSystem: Boolean,
                                        status: Int,
                                        `type`: Int,
                                        error: Boolean,
                                        code: Int)

  case class DropCollectionResponse(id: String,
                                    error: Boolean,
                                    code: Int)

  case class QueryRequest(query: String, count: Boolean, batchSize: Int)

  case class QueryResponse(result: List[Json],
                           hasMore: Boolean,
                           count: Int,
                           cached: Boolean,
                           extra: QueryResponseExtras,
                           error: Boolean,
                           code: Int)

  case class QueryResponseExtras(stats: QueryResponseStats, warnings: List[String])

  case class QueryResponseStats(writesExecuted: Int,
                                writesIgnored: Int,
                                scannedFull: Int,
                                scannedIndex: Int,
                                filtered: Int,
                                executionTime: Double)
}