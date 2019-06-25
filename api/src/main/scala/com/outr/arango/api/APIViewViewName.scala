package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIViewViewName {

  def delete(client: HttpClient, viewName: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/view/{view-name}".withArguments(Map("view-name" -> viewName)), append = true)
    .call[Json]


  def get(client: HttpClient, viewName: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/view/{view-name}".withArguments(Map("view-name" -> viewName)), append = true)
    .call[Json]
}