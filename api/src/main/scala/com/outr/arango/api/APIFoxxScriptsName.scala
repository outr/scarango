package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIFoxxScriptsName {

  def post(client: HttpClient, body: Value, name: String, mount: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Post)
    .path(path"/_api/foxx/scripts/{name}".withArguments(Map("name" -> name)), append = true)
    .params("mount" -> mount.toString)
    .restful[Value, Value](body)
}