package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIUserUserDatabaseDbname {

  def delete(client: HttpClient, user: String, dbname: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/user/{user}/database/{dbname}".withArguments(Map("user" -> user, "dbname" -> dbname)), append = true)
    .call[Json]


  def put(client: HttpClient, body: UserHandlingGrantDatabase, user: String, dbname: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/user/{user}/database/{dbname}".withArguments(Map("user" -> user, "dbname" -> dbname)), append = true)
    .restful[UserHandlingGrantDatabase, Json](body)
}