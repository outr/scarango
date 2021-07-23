package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIFoxxDependencies {

  def get(client: HttpClient, mount: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Get)
    .path(path"/_api/foxx/dependencies", append = true) 
    .params("mount" -> mount.toString)
    .call[Value]


  def patch(client: HttpClient, body: Value, mount: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Patch)
    .path(path"/_api/foxx/dependencies", append = true) 
    .params("mount" -> mount.toString)
    .restful[Value, Value](body)


  def put(client: HttpClient, body: Value, mount: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Put)
    .path(path"/_api/foxx/dependencies", append = true) 
    .params("mount" -> mount.toString)
    .restful[Value, Value](body)
}