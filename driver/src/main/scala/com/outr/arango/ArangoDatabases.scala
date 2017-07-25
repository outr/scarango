package com.outr.arango

import com.outr.arango.rest.{DatabaseInfo, Result}
import io.youi.http.Method
import io.circe.generic.auto._

import scala.concurrent.Future

class ArangoDatabases(session: ArangoSession) {
  def apply(name: String = Arango.defaultDatabase): ArangoDB = new ArangoDB(session, name)
  def current: Future[Result[DatabaseInfo]] = session.call[Result[DatabaseInfo]](None, "database/current", Method.Get)
  def list(accessibleOnly: Boolean = true): Future[Result[List[String]]] = {
    val path = if (accessibleOnly) {
      "database/user"
    } else {
      "database"
    }
    session.call[Result[List[String]]](None, path, Method.Get)
  }
}