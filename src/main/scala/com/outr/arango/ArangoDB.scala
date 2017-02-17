package com.outr.arango

import gigahorse.{Config, FullResponse}
import gigahorse.support.asynchttpclient.Gigahorse
import io.circe.{Decoder, Encoder, Json}
import io.circe.parser._
import io.circe.syntax._
import io.circe.generic.auto._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ArangoDB(baseURL: String) {
  protected def url(path: String): String = s"$baseURL$path"

  protected[arango] def defaultErrorHandler[Response]: FullResponse => Response = (response: FullResponse) => {
    throw new RuntimeException(s"Error from server: ${response.statusText} (${response.status}) with content: ${response.bodyAsString}")
  }

  protected[arango] def restful[Request, Response](path: String,
                                                   request: Request,
                                                   token: Option[String],
                                                   errorHandler: FullResponse => Response = defaultErrorHandler[Response])
                                                  (implicit encoder: Encoder[Request], decoder: Decoder[Response]): Future[Response] = {
    val http = Gigahorse.http(Config())
    val json = request.asJson.spaces2
    var req = Gigahorse.url(url(path)).post(json, Gigahorse.utf8)
    token.foreach { t =>
      req = req.withHeaders("Authorization" -> s"bearer $t")
    }
    val future = http.processFull(req).map { res =>
      val body = res.bodyAsString
      if (res.status >= 200 && res.status < 300) {
        decode[Response](body) match {
          case Left(error) => throw new RuntimeException(s"JSON decoding error: $body", error)
          case Right(result) => result
        }
      } else {
        errorHandler(res)
      }
    }
    future.onComplete { _ =>
      http.close()
    }
    future
  }

  def auth(username: String, password: String): Future[ArangoSession] = {
    restful[AuthenticationRequest, AuthenticationResponse]("/_open/auth", AuthenticationRequest(username, password), None).map { response =>
      new ArangoSession(this, response.jwt)
    }
  }

  case class AuthenticationRequest(username: String, password: String)

  case class AuthenticationResponse(jwt: String, must_change_password: Boolean)
}

class ArangoSession(val server: ArangoDB, val token: String) {
  def db(name: String): ArangoDBSession = new ArangoDBSession(this, name)
}

class ArangoDBSession(session: ArangoSession, db: String) {
  protected def restful[Request, Response](name: String,
                                           request: Request,
                                           errorHandler: FullResponse => Response = session.server.defaultErrorHandler[Response])
                                          (implicit encoder: Encoder[Request], decoder: Decoder[Response]): Future[Response] = {
    session.server.restful[Request, Response](s"/_db/$db/_api/$name", request, Some(session.token), errorHandler)
  }

  def parse(query: String): Future[ParseResult] = {
    restful[ParseRequest, ParseResult]("query", ParseRequest(query), (response) => {
      ParseResult(error = true, code = response.status, parsed = false, collections = Nil, bindVars = Nil, ast = Nil)
    })
  }

  def cursor(query: String, count: Boolean, batchSize: Int): Future[QueryResponse] = {
    restful[QueryRequest, QueryResponse]("cursor", QueryRequest(query, count, batchSize))
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