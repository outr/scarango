package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIGharialGraphVertexCollection {

  def delete(client: HttpClient, graph: String, collection: String, dropCollection: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[GeneralGraphVertexCollectionRemoveHttpExamplesRc200] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/gharial/{graph}/vertex/{collection}".withArguments(Map("graph" -> graph, "collection" -> collection)), append = true)
    .param[Option[Boolean]]("dropCollection", dropCollection, None)
    .call[GeneralGraphVertexCollectionRemoveHttpExamplesRc200]


  def post(client: HttpClient, graph: String, collection: String, waitForSync: Option[Boolean] = None, returnNew: Option[Boolean] = None, body: Value)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Post)
    .path(path"/_api/gharial/{graph}/vertex/{collection}".withArguments(Map("graph" -> graph, "collection" -> collection)), append = true)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("returnNew", returnNew, None)
    .restful[Value, Value](body)
}