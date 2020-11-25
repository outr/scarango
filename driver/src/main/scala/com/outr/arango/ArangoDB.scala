package com.outr.arango

import com.outr.arango.api._
import com.outr.arango.api.model.GetAPIDatabaseNew
import com.outr.arango.model.{ArangoResponse, DatabaseInfo}
import io.youi.client.{HttpClient, HttpClientConfig}
import io.youi.client.intercept.Interceptor
import io.youi.http.{Headers, HttpRequest, HttpResponse}
import io.youi.net._
import profig.{JsonUtil, Profig}
import reactify.{Val, Var}
import io.circe.parser._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

class ArangoDB(val database: String = ArangoDB.config.db,
               baseURL: URL = ArangoDB.config.url,
               credentials: Option[Credentials] = ArangoDB.credentials,
               httpClient: HttpClient = HttpClient) extends Interceptor {
  private[arango] val _state: Var[DatabaseState] = Var(DatabaseState.Uninitialized)
  def state: Val[DatabaseState] = _state
  def session: ArangoSession = state() match {
    case DatabaseState.Initialized(session, _) => session
    case DatabaseState.Error(t) => throw t
    case s => throw new RuntimeException(s"Not initialized: $s")
  }
  def client: HttpClient = session
    .client
    .interceptor(this)
    .dropNullValuesInJson(true)

  def init()(implicit ec: ExecutionContext): Future[DatabaseState] = {
    assert(state() == DatabaseState.Uninitialized, s"Cannot init, not in uninitialized state: ${state()}")
    _state := DatabaseState.Initializing
    val start = System.currentTimeMillis()
    val client = httpClient
      .url(baseURL)
      .path(Path.parse(s"/_db/$database/"))
      .noFailOnHttpStatus
    val futureSession = credentials match {
      case Some(c) => client
        .path(path"/_open/auth")
        .post
        .restful[Credentials, AuthenticationResponse](c)
        .map { r =>
          ArangoSession(client.header(Headers.Request.Authorization(s"bearer ${r.jwt}")))
        }
      case None => Future.successful(ArangoSession(client))
    }
    futureSession
      .map(session => DatabaseState.Initialized(session, System.currentTimeMillis() - start))
      .recover {
        case t: Throwable => DatabaseState.Error(t)
      }
      .map { state =>
        _state := state
        state
      }
  }

  object api {
    val system = new SystemDatabase(ArangoDB.this)

    object db extends ArangoDatabase(ArangoDB.this, client, database) {
      def current(implicit ec: ExecutionContext): Future[ArangoResponse[DatabaseInfo]] = APIDatabaseCurrent
        .get(client)
        .map(json => JsonUtil.fromJson[ArangoResponse[DatabaseInfo]](json))

      def apply(name: String): ArangoDatabase = new ArangoDatabase(ArangoDB.this, client.path(Path.parse(s"/_db/$name/")), name)
    }
  }

  def dispose(): Unit = _state := DatabaseState.Uninitialized

  override def before(request: HttpRequest): Future[HttpRequest] = Future.successful(request)

  override def after(request: HttpRequest, response: HttpResponse): Future[HttpResponse] = if (response.status.isSuccess) {
    Future.successful(response)
  } else {
    val content = response.content.getOrElse(throw new RuntimeException(s"No content for failed response: ${request.url} (${response.status})"))
    val json = parse(content.asString) match {
      case Left(pf) => throw pf
      case Right(j) => j
    }
    val error = JsonUtil.fromJson[ArangoError](json)
    throw new ArangoException(error, request, response, None)
  }

  override def toString: String = s"ArangoDB($database)"

  case class AuthenticationResponse(jwt: String, must_change_password: Option[Boolean] = None)
}

object ArangoDB {
  HttpClientConfig.default := HttpClientConfig(
    timeout = 5.minutes
  )

  def config: Config = Profig("arango").as[Config]

  def credentials: Option[Credentials] = if (config.authentication) {
    Some(config.credentials)
  } else {
    None
  }
}