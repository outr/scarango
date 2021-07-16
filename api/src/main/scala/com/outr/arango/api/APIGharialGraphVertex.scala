package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIGharialGraphVertex {

  def get(client: HttpClient, graph: String)(implicit ec: ExecutionContext): Future[GeneralGraphListVertexHttpExamplesRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_api/gharial/{graph}/vertex".withArguments(Map("graph" -> graph)), append = true)
    .call[GeneralGraphListVertexHttpExamplesRc200]


  def post(client: HttpClient, graph: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Post)
    .path(path"/_api/gharial/{graph}/vertex".withArguments(Map("graph" -> graph)), append = true)
    .call[Value]
}