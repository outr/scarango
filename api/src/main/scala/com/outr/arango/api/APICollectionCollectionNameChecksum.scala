package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APICollectionCollectionNameChecksum {

  def get(client: HttpClient, collectionName: String, withRevisions: Option[Boolean] = None, withData: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Get)
    .path(path"/_api/collection/{collection-name}/checksum".withArguments(Map("collection-name" -> collectionName)), append = true)
    .param[Option[Boolean]]("withRevisions", withRevisions, None)
    .param[Option[Boolean]]("withData", withData, None)
    .call[Value]
}