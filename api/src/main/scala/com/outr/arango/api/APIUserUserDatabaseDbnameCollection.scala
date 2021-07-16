package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIUserUserDatabaseDbnameCollection {

  def delete(client: HttpClient, user: String, dbname: String, collection: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/user/{user}/database/{dbname}/{collection}".withArguments(Map("user" -> user, "dbname" -> dbname, "collection" -> collection)), append = true)
    .call[Value]


  def put(client: HttpClient, body: UserHandlingGrantCollection, user: String, dbname: String, collection: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Put)
    .path(path"/_api/user/{user}/database/{dbname}/{collection}".withArguments(Map("user" -> user, "dbname" -> dbname, "collection" -> collection)), append = true)
    .restful[UserHandlingGrantCollection, Value](body)
}