package com.outr.arango

import com.outr.arango.api.{APIDatabase, APIDatabaseCurrent, APIDatabaseUser}
import com.outr.arango.model.{ArangoResponse, DatabaseInfo}
import io.circe.Json
import io.youi.client.{ClientException, HttpClient}
import io.youi.http.Headers
import io.youi.net._
import profig.{JsonUtil, Profig}
import reactify.{Val, Var}

import scala.concurrent.Future
import scribe.Execution.global

class ArangoDB(val database: String = ArangoDB.config.db,
               baseURL: URL = ArangoDB.config.url,
               credentials: Option[Credentials] = ArangoDB.credentials,
               httpClient: HttpClient = HttpClient) {
  private val _state: Var[DatabaseState] = Var(DatabaseState.Uninitialized)
  def state: Val[DatabaseState] = _state
  def session: ArangoDBSession = state() match {
    case DatabaseState.Initialized(session, _) => session
    case DatabaseState.Error(t) => throw t
    case s => throw new RuntimeException(s"Not initialized: $s")
  }
  def client: HttpClient = session.client

  def init(): Future[DatabaseState] = {
    assert(state() == DatabaseState.Uninitialized, s"Cannot init, not in uninitialized state: ${state()}")
    _state := DatabaseState.Initializing
    // TODO: Do upgrade
    val start = System.currentTimeMillis()
    val client = httpClient
      .url(baseURL)
      .path(Path.parse(s"/_db/$database/"))
    val futureSession = credentials match {
      case Some(c) => client
        .path(path"/_open/auth")
        .post
        .restful[Credentials, AuthenticationResponse](c)
        .map { r =>
          ArangoDBSession(client.header(Headers.Request.Authorization(s"bearer ${r.jwt}")))
        }
      case None => Future.successful(ArangoDBSession(client))
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
    object db {
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
    }
  }

  def dispose(): Unit = _state := DatabaseState.Uninitialized

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