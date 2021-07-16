package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIViewViewNameProperties {

  def get(client: HttpClient, viewName: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Get)
    .path(path"/_api/view/{view-name}/properties".withArguments(Map("view-name" -> viewName)), append = true)
    .call[Value]
}