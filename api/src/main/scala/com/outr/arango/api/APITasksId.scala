package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APITasksId {

  def delete(client: HttpClient, id: String)(implicit ec: ExecutionContext): Future[DeleteAPITasksRc200] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/tasks/{id}".withArguments(Map("id" -> id)), append = true)
    .call[DeleteAPITasksRc200]


  def get(client: HttpClient, id: String)(implicit ec: ExecutionContext): Future[APITaskStruct] = client
    .method(HttpMethod.Get)
    .path(path"/_api/tasks/{id}".withArguments(Map("id" -> id)), append = true)
    .call[APITaskStruct]


  def put(client: HttpClient, id: String, body: PutAPINewTasks)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/tasks/{id}".withArguments(Map("id" -> id)), append = true)
    .restful[PutAPINewTasks, Json](body)
}