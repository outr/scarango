package com.outr.arango

import io.youi.client.HttpClient
import io.youi.net._
import profig.Profig
import reactify.{Val, Var}

import scala.concurrent.Future

class ArangoDB(val database: String = ArangoDB.config.db,
               baseURL: URL = ArangoDB.config.url,
               credentials: Option[Credentials] = ArangoDB.credentials,
               httpClient: HttpClient = HttpClient) {
  private val url: URL = baseURL.withPath(Path.parse(s"/_db/$database/"))
  private val client: HttpClient = httpClient.url(url)

  private val _state: Var[DatabaseState] = Var(DatabaseState.Uninitialized)
  def state: Val[DatabaseState] = _state

  def init(): Future[DatabaseState] = {
    // TODO: implement with session init and database upgrade
  }
}

sealed trait DatabaseState

object DatabaseState {
  case object Uninitialized
  case object Initializing
  case class Initialized(session: ArangoDBSession, startupTime: Long)
  case class Error(throwable: Throwable)
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