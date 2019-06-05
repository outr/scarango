package com.outr.arango

import io.youi.client.HttpClient
import io.youi.http.Headers
import io.youi.net._
import profig.Profig
import reactify.{Val, Var}

import scala.concurrent.Future
import scribe.Execution.global

class ArangoDB(val database: String = ArangoDB.config.db,
               baseURL: URL = ArangoDB.config.url,
               credentials: Option[Credentials] = ArangoDB.credentials,
               httpClient: HttpClient = HttpClient) {
  private val url: URL = baseURL.withPath(Path.parse(s"/_db/$database/"))
  private val client: HttpClient = httpClient.url(url)

  private val _state: Var[DatabaseState] = Var(DatabaseState.Uninitialized)
  def state: Val[DatabaseState] = _state
  def session: ArangoDBSession = state() match {
    case DatabaseState.Initialized(session, _) => session
    case DatabaseState.Error(t) => throw t
    case s => throw new RuntimeException(s"Not initialized: $s")
  }

  def init(): Future[DatabaseState] = {
    assert(state() == DatabaseState.Uninitialized, s"Cannot init, not in uninitialized state: ${state()}")
    _state := DatabaseState.Initializing
    // TODO: Do upgrade
    val start = System.currentTimeMillis()
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
}

case class AuthenticationResponse(jwt: String, must_change_password: Option[Boolean] = None)

sealed trait DatabaseState

object DatabaseState {
  case object Uninitialized extends DatabaseState
  case object Initializing extends DatabaseState
  case object Upgrading extends DatabaseState
  case class Initialized(session: ArangoDBSession, startupTime: Long) extends DatabaseState
  case class Error(throwable: Throwable) extends DatabaseState
}

object ArangoDB {
  lazy val config: Config = Profig("arango").as[Config]

  def credentials: Option[Credentials] = if (config.authentication) {
    Some(config.credentials)
  } else {
    None
  }
}

case class ArangoDBSession(client: HttpClient)