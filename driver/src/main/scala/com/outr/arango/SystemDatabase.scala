package com.outr.arango

import com.outr.arango.api.model.GetAPIDatabaseNew
import com.outr.arango.api.{APIDatabase, APIDatabaseDatabaseName}
import com.outr.arango.model.ArangoResponse
import fabric.rw.Asable
import io.youi.net.Path

import scala.concurrent.{ExecutionContext, Future}

class SystemDatabase(db: ArangoDB) extends ArangoDatabase(db, db.client.path(Path.parse(s"/_db/_system")), "_system") {
  def create(databaseName: String)(implicit ec: ExecutionContext): Future[Boolean] = {
    APIDatabase.post(client, GetAPIDatabaseNew(
      name = databaseName
    )).map(_.as[ArangoResponse].value[Boolean])
    // TODO: Support setting user
  }

  def drop(databaseName: String)(implicit ec: ExecutionContext): Future[Boolean] = {
    APIDatabaseDatabaseName.delete(client, databaseName).map(_.as[ArangoResponse].value[Boolean])
  }
}
