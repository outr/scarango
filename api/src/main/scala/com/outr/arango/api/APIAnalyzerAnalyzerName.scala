package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIAnalyzerAnalyzerName {

  def delete(client: HttpClient, analyzerName: String, force: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/analyzer/{analyzer-name}".withArguments(Map("analyzer-name" -> analyzerName)), append = true)
    .param[Option[Boolean]]("force", force, None)
    .call[Json]


  def get(client: HttpClient, analyzerName: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/analyzer/{analyzer-name}".withArguments(Map("analyzer-name" -> analyzerName)), append = true)
    .call[Json]
}