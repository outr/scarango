package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIEdgesCollectionId {

  def get(client: HttpClient, collectionId: String, vertex: String, direction: Option[String] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/edges/{collection-id}".withArguments(Map("collection-id" -> collectionId)), append = true)
    .params("vertex" -> vertex.toString)
    .param[Option[String]]("direction", direction, None)
    .call[Json]
}