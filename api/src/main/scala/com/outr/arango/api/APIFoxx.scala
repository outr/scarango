package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIFoxx {

  def get(client: HttpClient, excludeSystem: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/foxx", append = true) 
    .param[Option[Boolean]]("excludeSystem", excludeSystem, None)
    .call[Json]


  def post(client: HttpClient, mount: String, development: Option[Boolean] = None, setup: Option[Boolean] = None, legacy: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/foxx", append = true) 
    .params("mount" -> mount.toString)
    .param[Option[Boolean]]("development", development, None)
    .param[Option[Boolean]]("setup", setup, None)
    .param[Option[Boolean]]("legacy", legacy, None)
    .call[Json]
}