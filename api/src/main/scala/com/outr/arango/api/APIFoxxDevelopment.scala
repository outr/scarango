package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIFoxxDevelopment {

  def delete(client: HttpClient, mount: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/foxx/development", append = true) 
    .params("mount" -> mount.toString)
    .call[Value]


  def post(client: HttpClient, mount: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Post)
    .path(path"/_api/foxx/development", append = true) 
    .params("mount" -> mount.toString)
    .call[Value]
}