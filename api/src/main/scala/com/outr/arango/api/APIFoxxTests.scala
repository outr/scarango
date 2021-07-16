package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIFoxxTests {

  def post(client: HttpClient, mount: String, reporter: Option[String] = None, idiomatic: Option[Boolean] = None, filter: Option[String] = None)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Post)
    .path(path"/_api/foxx/tests", append = true) 
    .params("mount" -> mount.toString)
    .param[Option[String]]("reporter", reporter, None)
    .param[Option[Boolean]]("idiomatic", idiomatic, None)
    .param[Option[String]]("filter", filter, None)
    .call[Value]
}