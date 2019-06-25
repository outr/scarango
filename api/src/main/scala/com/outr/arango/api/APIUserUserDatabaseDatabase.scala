package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIUserUserDatabaseDatabase {

  def get(client: HttpClient, user: String, database: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/user/{user}/database/{database}".withArguments(Map("user" -> user, "database" -> database)), append = true)
    .call[Json]
}