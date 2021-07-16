package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIGharialGraphEdge {

  def get(client: HttpClient, graph: String)(implicit ec: ExecutionContext): Future[GeneralGraphListEdgeHttpExamplesRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_api/gharial/{graph}/edge".withArguments(Map("graph" -> graph)), append = true)
    .call[GeneralGraphListEdgeHttpExamplesRc200]


  def post(client: HttpClient, graph: String, body: GeneralGraphEdgeDefinitionAddHttpExamples)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Post)
    .path(path"/_api/gharial/{graph}/edge".withArguments(Map("graph" -> graph)), append = true)
    .restful[GeneralGraphEdgeDefinitionAddHttpExamples, Value](body)
}