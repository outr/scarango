package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APITasks {

  def post(client: HttpClient, body: PostAPINewTasks)(implicit ec: ExecutionContext): Future[PostAPINewTasksRc200] = client
    .method(HttpMethod.Post)
    .path(path"/_api/tasks", append = true) 
    .restful[PostAPINewTasks, PostAPINewTasksRc200](body)


  def get(client: HttpClient)(implicit ec: ExecutionContext): Future[GetAPITasksAllRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_api/tasks/", append = true) 
    .call[GetAPITasksAllRc200]
}