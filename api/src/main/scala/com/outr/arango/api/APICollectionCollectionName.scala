package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APICollectionCollectionName {

  def delete(client: HttpClient, collectionName: String, isSystem: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/collection/{collection-name}".withArguments(Map("collection-name" -> collectionName)), append = true)
    .param[Option[Boolean]]("isSystem", isSystem, None)
    .call[Json]


  def get(client: HttpClient, collectionName: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/collection/{collection-name}".withArguments(Map("collection-name" -> collectionName)), append = true)
    .call[Json]
}