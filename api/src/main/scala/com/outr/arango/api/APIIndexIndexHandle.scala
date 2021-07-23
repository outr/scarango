package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIIndexIndexHandle {

  def delete(client: HttpClient, collection: String, indexHandle: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/index/{collection}/{index-handle}".withArguments(Map("collection" -> collection, "index-handle" -> indexHandle)), append = true)
    .call[Value]


  def get(client: HttpClient, indexHandle: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Get)
    .path(path"/_api/index/{index-handle}".withArguments(Map("index-handle" -> indexHandle)), append = true)
    .call[Value]
}