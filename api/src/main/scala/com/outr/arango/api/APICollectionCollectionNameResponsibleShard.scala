package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APICollectionCollectionNameResponsibleShard {

  def put(client: HttpClient, collectionName: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Put)
    .path(path"/_api/collection/{collection-name}/responsibleShard".withArguments(Map("collection-name" -> collectionName)), append = true)
    .call[Value]
}