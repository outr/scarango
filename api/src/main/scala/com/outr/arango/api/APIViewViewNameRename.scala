package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIViewViewNameRename {

  def put(client: HttpClient, viewName: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/view/{view-name}/rename".withArguments(Map("view-name" -> viewName)), append = true)
    .call[Json]
}