package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APICollectionCollectionNameFigures {

  def get(client: HttpClient, collectionName: String)(implicit ec: ExecutionContext): Future[GetAPICollectionFiguresRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_api/collection/{collection-name}/figures".withArguments(Map("collection-name" -> collectionName)), append = true)
    .call[GetAPICollectionFiguresRc200]
}