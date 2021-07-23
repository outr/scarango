package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIUserUser {

  def delete(client: HttpClient, user: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/user/{user}".withArguments(Map("user" -> user)), append = true)
    .call[Value]


  def get(client: HttpClient, user: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Get)
    .path(path"/_api/user/{user}".withArguments(Map("user" -> user)), append = true)
    .call[Value]


  def patch(client: HttpClient, user: String, body: UserHandlingModify)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Patch)
    .path(path"/_api/user/{user}".withArguments(Map("user" -> user)), append = true)
    .restful[UserHandlingModify, Value](body)


  def put(client: HttpClient, user: String, body: UserHandlingReplace)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Put)
    .path(path"/_api/user/{user}".withArguments(Map("user" -> user)), append = true)
    .restful[UserHandlingReplace, Value](body)
}