package com.outr.arango

import com.outr.arango.rest.{ParseRequest, ParseResult}
import io.circe.{Decoder, Encoder}
import io.circe.generic.auto._
import io.circe.generic.semiauto._
import io.youi.http.{HttpResponse, Method, StringContent}

import scala.concurrent.Future
import io.circe.parser._
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
      val responseJson = response.content.getOrElse(throw new RuntimeException(s"No content received in response.")) match {
        case content: StringContent => content.value
        case content => throw new RuntimeException(s"$content not supported")
      }
      decode[ArangoError](responseJson) match {
        case Left(error) => throw new RuntimeException(s"JSON decoding error: $responseJson", error)
        case Right(result) => ParseResult(
          error = result.error,
          errorMessage = Some(result.errorMessage),
          errorNum = Some(result.errorNum),
          code = response.status.code,
          parsed = false,
          collections = Nil,
          bindVars = Nil,
          ast = Nil
        )
      }
    })
  }
}

object ArangoSession {
  def default: Future[ArangoSession] = (new Arango).auth()
}