package com.outr.arango.api

import fabric.Value
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._

import scala.concurrent.{ExecutionContext, Future}
      
object APIAnalyzerAnalyzerName {

  def delete(client: HttpClient, analyzerName: String, force: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/analyzer/{analyzer-name}".withArguments(Map("analyzer-name" -> analyzerName)), append = true)
    .param[Option[Boolean]]("force", force, None)
    .call[Value]


  def get(client: HttpClient, analyzerName: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Get)
    .path(path"/_api/analyzer/{analyzer-name}".withArguments(Map("analyzer-name" -> analyzerName)), append = true)
    .call[Value]
}