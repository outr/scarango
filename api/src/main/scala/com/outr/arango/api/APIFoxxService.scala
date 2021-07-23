package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIFoxxService {

  def delete(client: HttpClient, mount: String, teardown: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/foxx/service", append = true) 
    .params("mount" -> mount.toString)
    .param[Option[Boolean]]("teardown", teardown, None)
    .call[Value]


  def get(client: HttpClient, mount: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Get)
    .path(path"/_api/foxx/service", append = true) 
    .params("mount" -> mount.toString)
    .call[Value]


  def patch(client: HttpClient, mount: String, teardown: Option[Boolean] = None, setup: Option[Boolean] = None, legacy: Option[Boolean] = None, force: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Patch)
    .path(path"/_api/foxx/service", append = true) 
    .params("mount" -> mount.toString)
    .param[Option[Boolean]]("teardown", teardown, None)
    .param[Option[Boolean]]("setup", setup, None)
    .param[Option[Boolean]]("legacy", legacy, None)
    .param[Option[Boolean]]("force", force, None)
    .call[Value]


  def put(client: HttpClient, mount: String, teardown: Option[Boolean] = None, setup: Option[Boolean] = None, legacy: Option[Boolean] = None, force: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Put)
    .path(path"/_api/foxx/service", append = true) 
    .params("mount" -> mount.toString)
    .param[Option[Boolean]]("teardown", teardown, None)
    .param[Option[Boolean]]("setup", setup, None)
    .param[Option[Boolean]]("legacy", legacy, None)
    .param[Option[Boolean]]("force", force, None)
    .call[Value]
}