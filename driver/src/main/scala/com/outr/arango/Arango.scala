package com.outr.arango

import com.outr.arango.rest.{AuthenticationRequest, AuthenticationResponse}
import io.circe.{Decoder, Encoder, Json}
import io.circe.generic.auto._
import io.circe.parser.decode
import io.youi.client.{ErrorHandler, HttpClient}
import io.youi.http.{Headers, HttpRequest, HttpResponse, Method, RequestContent, StringContent}
import io.youi.net.URL
import profig.Config

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class Arango(baseURL: URL = Arango.defaultURL) {
  private var disposed: Boolean = false
  private val client = new HttpClient

  protected[arango] def defaultErrorHandler[Request, Response](request: Request): ErrorHandler[Response] = new ErrorHandler[Response] {
    override def apply(request: HttpRequest, response: HttpResponse, throwable: Option[Throwable]): Response = {
      val content = response.content.map(_.asInstanceOf[StringContent].value).getOrElse("")
      val (error: ArangoError, cause: Option[Throwable]) = decode[ArangoError](content) match {
        case Left(exc) => {
          val t = throwable.getOrElse(exc)
          ArangoError(
            error = true,
            code = response.status.code,
            errorNum = Some(ArangoCode.Failed.code),
            errorMessage = t.getMessage
          ) -> Some(t)
        }
        case Right(ae) => ae -> None
      }
      throw new ArangoException(error, request, response, cause)
    }
  }

  protected[arango] def send(path: String,
                             method: Method = Method.Get,
                             token: Option[String] = None,
                             params: Map[String, String] = Map.empty,
                             content: Option[RequestContent] = None): Future[HttpResponse] = {
    val headers = token.map(t => Headers.empty.withHeader(Headers.Request.Authorization(s"bearer $t"))).getOrElse(Headers.empty)
    val url = baseURL.withPath(path).withParams(params)
    val request = HttpRequest(method, url = url, headers = headers, content = content)
    client.send(request)
  }

  protected[arango] def restful[Request, Response](path: String,
                                                   request: Request,
                                                   token: Option[String],
                                                   params: Map[String, String] = Map.empty,
                                                   errorHandler: Option[ErrorHandler[Response]] = None,
                                                   method: Method = Method.Post,
                                                   anchor: Option[String] = None)
                                                  (implicit encoder: Encoder[Request], decoder: Decoder[Response]): Future[Response] = {
    val headers = token.map(t => Headers.empty.withHeader(Headers.Request.Authorization(s"bearer $t"))).getOrElse(Headers.empty)
    val url = baseURL.withPath(path).withParams(params).copy(fragment = anchor)
    val processor = (json: Json) => {
      def removeEmpty(fieldName: String, json: Json): Json = {
        json.hcursor.downField(fieldName).as[Option[String]] match {
          case Left(_) => json
          case Right(option) => if (option.isEmpty) {
            json.mapObject(_.remove(fieldName))
          } else {
            json
          }
        }
      }

      def removeAll(json: Json): Json = {
        removeEmpty("_key", removeEmpty("_id", removeEmpty("_rev", json)))
      }

      if (json.isArray) {
        val list = json.as[List[Json]].getOrElse(throw new RuntimeException("Something went wrong"))
        val modified = list.map(removeAll)
        Json.fromValues(modified)
      } else {
        removeAll(json)
      }
    }
    client.restful[Request, Response](url, request, headers, errorHandler.getOrElse(defaultErrorHandler(request)), method, processor)
  }

  protected[arango] def call[Response](path: String,
                                       method: Method,
                                       token: Option[String],
                                       params: Map[String, String] = Map.empty,
                                       errorHandler: Option[ErrorHandler[Response]] = None)
                                      (implicit decoder: Decoder[Response]): Future[Response] = {
    val headers = token.map(t => Headers.empty.withHeader(Headers.Request.Authorization(s"bearer $t"))).getOrElse(Headers.empty)
    val url = baseURL.withPath(path).withParams(params)
    client.call[Response](url, method, headers, errorHandler.getOrElse(defaultErrorHandler(path)))
  }

  def session(credentials: Option[Credentials] = Arango.defaultCredentials): Future[ArangoSession] = credentials match {
    case Some(Credentials(username, password)) => restful[AuthenticationRequest, AuthenticationResponse](
      path = "/_open/auth",
      request = AuthenticationRequest(username, password),
      token = None
    ).map { response =>
      new ArangoSession(this, Option(response.jwt), credentials)
    }
    case None => Future.successful(new ArangoSession(this, None, credentials))
  }

  def isDisposed: Boolean = disposed

  def dispose(): Unit = {
    client.dispose()
    disposed = true
  }
}

object Arango {
  Config.init(Nil)   // Make sure Profig is initialized

  private lazy val config = Config("arango")

  def defaultDatabase: String = config("db").as[String]
  def defaultURL: URL = URL(config("url").as[String])
  def defaultAuthentication: Boolean = config("authentication").as[Boolean]
  def defaultUsername: String = config("username").as[String]
  def defaultPassword: String = config("password").as[String]

  def defaultCredentials: Option[Credentials] = if (defaultAuthentication) {
    Some(Credentials(defaultUsername, defaultPassword))
  } else {
    None
  }

  def synchronous[T](future: Future[T], timeout: FiniteDuration = 10.seconds): T = try {
    Await.result(future, timeout)
  } catch {
    case t: Throwable => throw new RuntimeException("Error while executing asynchronously", t)
  }
}