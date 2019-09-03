package com.outr.arango

import com.outr.arango.api.model.GetAPIDatabaseNew
import com.outr.arango.api.{APIDatabase, APIDatabaseDatabaseName}
import com.outr.arango.model.ArangoResponse
import io.youi.net.Path
import profig.JsonUtil

import scala.concurrent.{ExecutionContext, Future}

class SystemDatabase(db: ArangoDB) extends ArangoDatabase(db, db.client.path(Path.parse(s"/_db/_system")), "_system") {
  def create(databaseName: String)(implicit ec: ExecutionContext): Future[ArangoResponse[Boolean]] = {
    APIDatabase.post(client, GetAPIDatabaseNew(
      name = databaseName
    )).map(json => JsonUtil.fromJson[ArangoResponse[Boolean]](json))
    // TODO: Support setting user
  }

  def drop(databaseName: String)(implicit ec: ExecutionContext): Future[ArangoResponse[Boolean]] = {
    APIDatabaseDatabaseName.delete(client, databaseName).map(json => JsonUtil.fromJson[ArangoResponse[Boolean]](json))
  }
}
