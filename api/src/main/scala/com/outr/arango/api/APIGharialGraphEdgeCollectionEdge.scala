package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIGharialGraphEdgeCollectionEdge {

  def delete(client: HttpClient, graph: String, collection: String, edge: String, waitForSync: Option[Boolean] = None, returnOld: Option[Boolean] = None, ifMatch: Option[String] = None)(implicit ec: ExecutionContext): Future[GeneralGraphEdgeDeleteHttpExamplesRc200] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/gharial/{graph}/edge/{collection}/{edge}".withArguments(Map("graph" -> graph, "collection" -> collection, "edge" -> edge)), append = true)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .call[GeneralGraphEdgeDeleteHttpExamplesRc200]


  def get(client: HttpClient, graph: String, collection: String, edge: String, rev: Option[String] = None, ifMatch: Option[String] = None, ifNoneMatch: Option[String] = None)(implicit ec: ExecutionContext): Future[GeneralGraphEdgeGetHttpExamplesRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_api/gharial/{graph}/edge/{collection}/{edge}".withArguments(Map("graph" -> graph, "collection" -> collection, "edge" -> edge)), append = true)
    .param[Option[String]]("rev", rev, None)
    .call[GeneralGraphEdgeGetHttpExamplesRc200]


  def patch(client: HttpClient, graph: String, collection: String, edge: String, waitForSync: Option[Boolean] = None, keepNull: Option[Boolean] = None, returnOld: Option[Boolean] = None, returnNew: Option[Boolean] = None, ifMatch: Option[String] = None, body: Json)(implicit ec: ExecutionContext): Future[GeneralGraphEdgeModifyHttpExamplesRc200] = client
    .method(HttpMethod.Patch)
    .path(path"/_api/gharial/{graph}/edge/{collection}/{edge}".withArguments(Map("graph" -> graph, "collection" -> collection, "edge" -> edge)), append = true)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("keepNull", keepNull, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("returnNew", returnNew, None)
    .restful[Json, GeneralGraphEdgeModifyHttpExamplesRc200](body)


  def put(client: HttpClient, graph: String, collection: String, edge: String, waitForSync: Option[Boolean] = None, keepNull: Option[Boolean] = None, returnOld: Option[Boolean] = None, returnNew: Option[Boolean] = None, ifMatch: Option[String] = None, body: GeneralGraphEdgeReplaceHttpExamples)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/gharial/{graph}/edge/{collection}/{edge}".withArguments(Map("graph" -> graph, "collection" -> collection, "edge" -> edge)), append = true)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("keepNull", keepNull, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("returnNew", returnNew, None)
    .restful[GeneralGraphEdgeReplaceHttpExamples, Json](body)
}