package com.outr.arango

import com.outr.arango.rest.{AuthenticationRequest, AuthenticationResponse}
import com.typesafe.config.ConfigFactory
import io.circe.{Decoder, Encoder}
import io.circe.generic.auto._
import io.circe.parser.decode
import io.youi.client.HttpClient
import io.youi.http.{Headers, HttpResponse, Method, StringContent}
import io.youi.net.URL

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class Arango(baseURL: URL = Arango.defaultURL) {
  private var disposed: Boolean = false
  private val client = new HttpClient

  protected[arango] def defaultErrorHandler[Request, Response](request: Request): HttpResponse => Response = (response: HttpResponse) => {
    val content = response.content.get.asInstanceOf[StringContent].value
    decode[ArangoError](content) match {
      case Left(_) => throw new RuntimeException(s"Error from server: ${response.status} with content: ${response.content}")
      case Right(error) => throw new ArangoException(error, response.status.message, request)
    }
  }

  protected[arango] def restful[Request, Response](path: String,
                                                   request: Request,
                                                   token: Option[String],
                                                   params: Map[String, String] = Map.empty,
                                                   errorHandler: Option[HttpResponse => Response] = None,
                                                   method: Method = Method.Post)
                                                  (implicit encoder: Encoder[Request], decoder: Decoder[Response]): Future[Response] = {
    val headers = token.map(t => Headers.empty.withHeader(Headers.Request.Authorization(s"bearer $t"))).getOrElse(Headers.empty)
    val url = baseURL.withPath(path).withParams(params)
    client.restful[Request, Response](url, request, headers, errorHandler.getOrElse(defaultErrorHandler(request)), method)
  }

  protected[arango] def call[Response](path: String,
                                       method: Method,
                                       token: Option[String],
                                       params: Map[String, String] = Map.empty,
                                       errorHandler: Option[HttpResponse => Response] = None)
                                      (implicit decoder: Decoder[Response]): Future[Response] = {
    val headers = token.map(t => Headers.empty.withHeader(Headers.Request.Authorization(s"bearer $t"))).getOrElse(Headers.empty)
    val url = baseURL.withPath(path).withParams(params)
    client.call[Response](url, method, headers, errorHandler.getOrElse(defaultErrorHandler(path)))
  }

  def auth(username: String = Arango.defaultUsername,
           password: String = Arango.defaultPassword): Future[ArangoSession] = {
    restful[AuthenticationRequest, AuthenticationResponse]("/_open/auth", AuthenticationRequest(username, password), None).map { response =>
      new ArangoSession(this, Option(response.jwt))
    }
  }

  def noAuth(): Future[ArangoSession] = Future.successful(new ArangoSession(this, None))

  def isDisposed: Boolean = disposed

  def dispose(): Unit = {
    client.dispose()
    disposed = true
  }
}

object Arango {
  private val config = ConfigFactory.load()

  var defaultDatabase: String = value("Arango.db")
  var defaultURL: URL = URL(value("Arango.url"))
  var defaultAuthentication: Boolean = value("Arango.authentication").toBoolean
  var defaultUsername: String = value("Arango.username")
  var defaultPassword: String = value("Arango.password")

  private def value(path: String): String = {
    val envName = path.toUpperCase.replace('.', '_')
    Option(System.getenv(envName)).getOrElse(config.getString(path))
  }
}