package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIGharialGraphVertexCollectionVertex {

  def delete(client: HttpClient, graph: String, collection: String, vertex: String, waitForSync: Option[Boolean] = None, returnOld: Option[Boolean] = None, ifMatch: Option[String] = None)(implicit ec: ExecutionContext): Future[GeneralGraphVertexDeleteHttpExamplesRc200] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/gharial/{graph}/vertex/{collection}/{vertex}".withArguments(Map("graph" -> graph, "collection" -> collection, "vertex" -> vertex)), append = true)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .call[GeneralGraphVertexDeleteHttpExamplesRc200]


  def get(client: HttpClient, graph: String, collection: String, vertex: String, rev: Option[String] = None, ifMatch: Option[String] = None, ifNoneMatch: Option[String] = None)(implicit ec: ExecutionContext): Future[GeneralGraphVertexGetHttpExamplesRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_api/gharial/{graph}/vertex/{collection}/{vertex}".withArguments(Map("graph" -> graph, "collection" -> collection, "vertex" -> vertex)), append = true)
    .param[Option[String]]("rev", rev, None)
    .call[GeneralGraphVertexGetHttpExamplesRc200]


  def patch(client: HttpClient, graph: String, collection: String, vertex: String, waitForSync: Option[Boolean] = None, keepNull: Option[Boolean] = None, returnOld: Option[Boolean] = None, returnNew: Option[Boolean] = None, ifMatch: Option[String] = None, body: Value)(implicit ec: ExecutionContext): Future[GeneralGraphVertexModifyHttpExamplesRc200] = client
    .method(HttpMethod.Patch)
    .path(path"/_api/gharial/{graph}/vertex/{collection}/{vertex}".withArguments(Map("graph" -> graph, "collection" -> collection, "vertex" -> vertex)), append = true)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("keepNull", keepNull, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("returnNew", returnNew, None)
    .restful[Value, GeneralGraphVertexModifyHttpExamplesRc200](body)


  def put(client: HttpClient, graph: String, collection: String, vertex: String, waitForSync: Option[Boolean] = None, keepNull: Option[Boolean] = None, returnOld: Option[Boolean] = None, returnNew: Option[Boolean] = None, ifMatch: Option[String] = None, body: Value)(implicit ec: ExecutionContext): Future[GeneralGraphVertexReplaceHttpExamplesRc200] = client
    .method(HttpMethod.Put)
    .path(path"/_api/gharial/{graph}/vertex/{collection}/{vertex}".withArguments(Map("graph" -> graph, "collection" -> collection, "vertex" -> vertex)), append = true)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("keepNull", keepNull, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("returnNew", returnNew, None)
    .restful[Value, GeneralGraphVertexReplaceHttpExamplesRc200](body)
}