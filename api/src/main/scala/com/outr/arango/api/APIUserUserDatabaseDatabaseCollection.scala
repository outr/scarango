package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIUserUserDatabaseDatabaseCollection {

  def get(client: HttpClient, user: String, database: String, collection: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Get)
    .path(path"/_api/user/{user}/database/{database}/{collection}".withArguments(Map("user" -> user, "database" -> database, "collection" -> collection)), append = true)
    .call[Value]
}