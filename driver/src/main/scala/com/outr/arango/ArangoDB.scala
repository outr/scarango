package com.outr.arango

import com.outr.arango.api._
import com.outr.arango.model.{ArangoResponse, DatabaseInfo}
import io.youi.client.HttpClient
import io.youi.client.intercept.Interceptor
import io.youi.http.{Headers, HttpRequest, HttpResponse}
import io.youi.net._
import profig.{JsonUtil, Profig}
import reactify.{Val, Var}
import scribe.Execution.global
import io.circe.parser._

import scala.concurrent.Future

class ArangoDB(val database: String = ArangoDB.config.db,
               baseURL: URL = ArangoDB.config.url,
               credentials: Option[Credentials] = ArangoDB.credentials,
               httpClient: HttpClient = HttpClient) extends Interceptor {
  private val _state: Var[DatabaseState] = Var(DatabaseState.Uninitialized)
  def state: Val[DatabaseState] = _state
  def session: ArangoSession = state() match {
    case DatabaseState.Initialized(session, _) => session
    case DatabaseState.Error(t) => throw t
    case s => throw new RuntimeException(s"Not initialized: $s")
  }
  def client: HttpClient = session.client.interceptor(this)

  def init(): Future[DatabaseState] = {
    assert(state() == DatabaseState.Uninitialized, s"Cannot init, not in uninitialized state: ${state()}")
    _state := DatabaseState.Initializing
    // TODO: Do upgrade
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
    object db extends ArangoDatabase(client, database) {
      def current: Future[ArangoResponse[DatabaseInfo]] = APIDatabaseCurrent
        .get(client)
        .map(JsonUtil.fromJson[ArangoResponse[DatabaseInfo]](_))

      def list(accessibleOnly: Boolean = true): Future[ArangoResponse[List[String]]] = {
        val future = if (accessibleOnly) {
          APIDatabaseUser.get(client)
        } else {
          APIDatabase.get(client)
        }
        future.map(JsonUtil.fromJson[ArangoResponse[List[String]]](_))
      }

      def apply(name: String): ArangoDatabase = new ArangoDatabase(client, name)
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

  case class AuthenticationResponse(jwt: String, must_change_password: Option[Boolean] = None)
}

object ArangoDB {
  def config: Config = Profig("arango").as[Config]

  def credentials: Option[Credentials] = if (config.authentication) {
    Some(config.credentials)
  } else {
    None
  }
}