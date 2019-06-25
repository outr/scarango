package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIDocumentDocumentHandle {

  def put(client: HttpClient, body: Json, documentHandle: String, waitForSync: Option[Boolean] = None, ignoreRevs: Option[Boolean] = None, returnOld: Option[Boolean] = None, returnNew: Option[Boolean] = None, silent: Option[Boolean] = None, IfMatch: Option[String] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/document/{document-handle}".withArguments(Map("document-handle" -> documentHandle)), append = true)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("ignoreRevs", ignoreRevs, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("returnNew", returnNew, None)
    .param[Option[Boolean]]("silent", silent, None)
    .restful[Json, Json](body)


  def patch(client: HttpClient, body: Json, documentHandle: String, keepNull: Option[Boolean] = None, mergeObjects: Option[Boolean] = None, waitForSync: Option[Boolean] = None, ignoreRevs: Option[Boolean] = None, returnOld: Option[Boolean] = None, returnNew: Option[Boolean] = None, silent: Option[Boolean] = None, IfMatch: Option[String] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Patch)
    .path(path"/_api/document/{document-handle}".withArguments(Map("document-handle" -> documentHandle)), append = true)
    .param[Option[Boolean]]("keepNull", keepNull, None)
    .param[Option[Boolean]]("mergeObjects", mergeObjects, None)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("ignoreRevs", ignoreRevs, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("returnNew", returnNew, None)
    .param[Option[Boolean]]("silent", silent, None)
    .restful[Json, Json](body)


  def delete(client: HttpClient, collectionName: String, documentHandle: String, waitForSync: Option[Boolean] = None, returnOld: Option[Boolean] = None, silent: Option[Boolean] = None, IfMatch: Option[String] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/document/{collection}/{document-handle}".withArguments(Map("collection" -> collectionName, "document-handle" -> documentHandle)), append = true)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("silent", silent, None)
    .call[Json]


  def get(client: HttpClient, collection: String, documentHandle: String, IfNoneMatch: Option[String] = None, IfMatch: Option[String] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/document/{collection}/{document-handle}".withArguments(Map("collection" -> collection, "document-handle" -> documentHandle)), append = true)
    .call[Json]


  def head(client: HttpClient, documentHandle: String, IfNoneMatch: Option[String] = None, IfMatch: Option[String] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Head)
    .path(path"/_api/document/{document-handle}".withArguments(Map("document-handle" -> documentHandle)), append = true)
    .call[Json]
}