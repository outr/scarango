package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIUserUserDatabaseDbname {

  def delete(client: HttpClient, user: String, dbname: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/user/{user}/database/{dbname}".withArguments(Map("user" -> user, "dbname" -> dbname)), append = true)
    .call[Value]


  def put(client: HttpClient, body: UserHandlingGrantDatabase, user: String, dbname: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Put)
    .path(path"/_api/user/{user}/database/{dbname}".withArguments(Map("user" -> user, "dbname" -> dbname)), append = true)
    .restful[UserHandlingGrantDatabase, Value](body)
}