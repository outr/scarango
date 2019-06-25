package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIGharialGraphEdgeCollection {

  def post(client: HttpClient, graph: String, collection: String, waitForSync: Option[Boolean] = None, returnNew: Option[Boolean] = None, body: GeneralGraphEdgeCreateHttpExamples)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/gharial/{graph}/edge/{collection}".withArguments(Map("graph" -> graph, "collection" -> collection)), append = true)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("returnNew", returnNew, None)
    .restful[GeneralGraphEdgeCreateHttpExamples, Json](body)
}