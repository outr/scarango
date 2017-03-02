package com.outr.arango

import io.circe.{Decoder, Encoder, Json}
import io.circe.generic.auto._
import io.youi.client.HttpClient
import io.youi.http.{Headers, HttpResponse}
import io.youi.net.URL

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ArangoDB(baseURL: String) {
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

  def auth(username: String, password: String): Future[ArangoSession] = {
    restful[AuthenticationRequest, AuthenticationResponse]("/_open/auth", AuthenticationRequest(username, password), None).map { response =>
      new ArangoSession(this, response.jwt)
    }
  }

  case class AuthenticationRequest(username: String, password: String)

  case class AuthenticationResponse(jwt: String, must_change_password: Boolean)

  def dispose(): Unit = client.dispose()
}

class ArangoSession(val server: ArangoDB, val token: String) {
  protected def restful[Request, Response](name: String,
                                           request: Request,
                                           errorHandler: HttpResponse => Response = server.defaultErrorHandler[Response])
                                          (implicit encoder: Encoder[Request], decoder: Decoder[Response]): Future[Response] = {
    server.restful[Request, Response](s"/_api/$name", request, Some(token), errorHandler)
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

  def cursor(query: String, count: Boolean, batchSize: Int): Future[QueryResponse] = {
    restful[QueryRequest, QueryResponse]("cursor", QueryRequest(query, count, batchSize))
  }

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