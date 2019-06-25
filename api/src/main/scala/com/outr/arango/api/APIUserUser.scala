package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIUserUser {

  def delete(client: HttpClient, user: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/user/{user}".withArguments(Map("user" -> user)), append = true)
    .call[Json]


  def get(client: HttpClient, user: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/user/{user}".withArguments(Map("user" -> user)), append = true)
    .call[Json]


  def patch(client: HttpClient, user: String, body: UserHandlingModify)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Patch)
    .path(path"/_api/user/{user}".withArguments(Map("user" -> user)), append = true)
    .restful[UserHandlingModify, Json](body)


  def put(client: HttpClient, user: String, body: UserHandlingReplace)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/user/{user}".withArguments(Map("user" -> user)), append = true)
    .restful[UserHandlingReplace, Json](body)
}