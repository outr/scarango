package com.outr.arango

import com.outr.arango.rest.{ParseRequest, ParseResult}
import io.circe.{Decoder, Encoder}
import io.circe.generic.auto._
import io.circe.generic.semiauto._
import io.youi.http.{HttpResponse, Method}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ArangoSession(val instance: Arango, val token: String) {
  protected def restful[Request, Response](name: String,
                                           request: Request,
                                           params: Map[String, String] = Map.empty,
                                           errorHandler: HttpResponse => Response = instance.defaultErrorHandler[Response])
                                          (implicit encoder: Encoder[Request], decoder: Decoder[Response]): Future[Response] = {
    instance.restful[Request, Response](s"/_api/$name", request, Some(token), params, errorHandler)
  }

  protected def call[Response](name: String,
                               method: Method,
                               params: Map[String, String] = Map.empty,
                               errorHandler: HttpResponse => Response = instance.defaultErrorHandler[Response])
                              (implicit decoder: Decoder[Response]): Future[Response] = {
    instance.call[Response](s"/_api/$name", method, Some(token), params, errorHandler)
  }

  def db(name: String): ArangoDB = new ArangoDB(this, name)

  def parse(query: String): Future[ParseResult] = {
    implicit val decoder: Decoder[ParseResult] = deriveDecoder[ParseResult]

    restful[ParseRequest, ParseResult]("query", ParseRequest(query), errorHandler = (response) => {
      ParseResult(error = true, code = response.status.code, parsed = false, collections = Nil, bindVars = Nil, ast = Nil)
    })
  }
}

object ArangoSession {
  def default: Future[ArangoSession] = (new Arango).auth()
}