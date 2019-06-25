package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APICollectionCollectionNameProperties {

  def get(client: HttpClient, collectionName: String)(implicit ec: ExecutionContext): Future[CollectionInfo] = client
    .method(HttpMethod.Get)
    .path(path"/_api/collection/{collection-name}/properties".withArguments(Map("collection-name" -> collectionName)), append = true)
    .call[CollectionInfo]


  def put(client: HttpClient, collectionName: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/collection/{collection-name}/properties".withArguments(Map("collection-name" -> collectionName)), append = true)
    .call[Json]
}