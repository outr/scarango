package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIGharialGraphEdgeDefinition {

  def delete(client: HttpClient, graph: String, definition: String, waitForSync: Option[Boolean] = None, dropCollections: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/gharial/{graph}/edge/{definition}".withArguments(Map("graph" -> graph, "definition" -> definition)), append = true)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("dropCollections", dropCollections, None)
    .call[Json]


  def put(client: HttpClient, graph: String, definition: String, waitForSync: Option[Boolean] = None, dropCollections: Option[Boolean] = None, body: GeneralGraphEdgeDefinitionModifyHttpExamples)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/gharial/{graph}/edge/{definition}".withArguments(Map("graph" -> graph, "definition" -> definition)), append = true)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("dropCollections", dropCollections, None)
    .restful[GeneralGraphEdgeDefinitionModifyHttpExamples, Json](body)
}