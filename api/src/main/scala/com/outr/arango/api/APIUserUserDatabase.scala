package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIUserUserDatabase {

  def get(client: HttpClient, user: String, full: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Get)
    .path(path"/_api/user/{user}/database/".withArguments(Map("user" -> user)), append = true)
    .param[Option[Boolean]]("full", full, None)
    .call[Value]
}