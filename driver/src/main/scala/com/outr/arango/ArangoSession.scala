package com.outr.arango

import com.outr.arango.rest.{ParseRequest, ParseResult}
import io.circe.{Decoder, Encoder}
import io.circe.generic.auto._
import io.circe.generic.semiauto._
import io.youi.http.{HttpRequest, HttpResponse, Method, RequestContent, Status, StringContent}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import io.circe.parser._
import io.youi.client.ErrorHandler

class ArangoSession(val instance: Arango, var token: Option[String], credentials: Option[Credentials]) {
  protected[arango] def restful[Request, Response](db: Option[String],
                                                   name: String,
                                                   request: Request,
                                                   params: Map[String, String] = Map.empty,
                                                   errorHandler: Option[ErrorHandler[Response]] = None,
                                                   method: Method = Method.Post,
                                                   retryAttempts: Int = 1)
                                                  (implicit encoder: Encoder[Request],
                                                            decoder: Decoder[Response]): Future[Response] = {
    val path = db match {
      case Some(value) => s"/_db/$value/_api/$name"
      case None => s"/_api/$name"
    }
    val fallbackHandler = new ErrorHandler[Response] {
      override def apply(httpRequest: HttpRequest, httpResponse: HttpResponse, throwable: Option[Throwable]): Response = {
        if (httpResponse.status == Status.Unauthorized && retryAttempts > 0) {     // Attempt reconnect
          scribe.warn("Unauthorized response! Attempting reconnect...")
          val newSession = Await.result(instance.session(credentials), 10.seconds)
          token = newSession.token
          scribe.info(s"Reconnect successful. Retrying $method to $path...")
          Await.result(restful[Request, Response](db, name, request, params, errorHandler, method, retryAttempts - 1)(encoder, decoder), Duration.Inf)
        } else {
          errorHandler.getOrElse(instance.defaultErrorHandler(request))(httpRequest, httpResponse, throwable)
        }
      }
    }

    instance.restful[Request, Response](path, request, token, params, Some(fallbackHandler), method)
  }

  protected[arango] def call[Response](db: Option[String],
                                       name: String,
                                       method: Method,
                                       params: Map[String, String] = Map.empty,
                                       errorHandler: Option[ErrorHandler[Response]] = None,
                                       retryAttempts: Int = 1)
                                      (implicit decoder: Decoder[Response]): Future[Response] = {
    val path = db match {
      case Some(value) => s"/_db/$value/_api/$name"
      case None => s"/_api/$name"
    }
    val fallbackHandler = new ErrorHandler[Response] {
      override def apply(httpRequest: HttpRequest, httpResponse: HttpResponse, throwable: Option[Throwable]): Response = {
        if (httpResponse.status == Status.Unauthorized && retryAttempts > 0) {     // Attempt reconnect
          scribe.warn("Unauthorized response! Attempting reconnect...")
          val newSession = Await.result(instance.session(credentials), 10.seconds)
          token = newSession.token
          Await.result(call[Response](db, name, method, params, errorHandler, retryAttempts - 1)(decoder), Duration.Inf)
        } else {
          errorHandler.getOrElse(instance.defaultErrorHandler(path))(httpRequest, httpResponse, throwable)
        }
      }
    }

    instance.call[Response](path, method, token, params, Some(fallbackHandler))
  }

  protected[arango] def send(db: Option[String],
                             name: String,
                             method: Method = Method.Get,
                             params: Map[String, String] = Map.empty,
                             content: Option[RequestContent] = None): Future[HttpResponse] = {
    val path = db match {
      case Some(value) => s"/_db/$value/_api/$name"
      case None => s"/_api/$name"
    }
    instance.send(path, method, token, params, content)
  }

  lazy val db = new ArangoDatabases(this)

  def parse(query: String): Future[ParseResult] = {
    implicit val decoder: Decoder[ParseResult] = deriveDecoder[ParseResult]

    val errorHandler = new ErrorHandler[ParseResult] {
      override def apply(request: HttpRequest, response: HttpResponse, throwable: Option[Throwable]): ParseResult = {
        val responseJson = response.content.getOrElse(throw new RuntimeException(s"No content received in response.")) match {
          case content: StringContent => content.value
          case content => throw new RuntimeException(s"$content not supported")
        }
        decode[ArangoError](responseJson) match {
          case Left(error) => throw new RuntimeException(s"JSON decoding error: $responseJson (${request.url})", error)
          case Right(result) => ParseResult(
            error = result.error,
            errorMessage = Some(result.errorMessage),
            errorNum = result.errorNum,
            code = response.status.code,
            parsed = false,
            collections = Nil,
            bindVars = Nil,
            ast = Nil
          )
        }
      }
    }

    restful[ParseRequest, ParseResult](None, "query", ParseRequest(query), errorHandler = Some(errorHandler))
  }
}

object ArangoSession {
  def default: Future[ArangoSession] = {
    val arango = new Arango
    arango.session(Arango.defaultCredentials)
  }
}