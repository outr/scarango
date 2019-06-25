package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APICollection {

  def get(client: HttpClient, excludeSystem: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/collection", append = true) 
    .param[Option[Boolean]]("excludeSystem", excludeSystem, None)
    .call[Json]


  def post(client: HttpClient, body: PostAPICollection, waitForSyncReplication: Option[Int] = None, enforceReplicationFactor: Option[Int] = None)(implicit ec: ExecutionContext): Future[CollectionInfo] = client
    .method(HttpMethod.Post)
    .path(path"/_api/collection", append = true) 
    .param[Option[Int]]("waitForSyncReplication", waitForSyncReplication, None)
    .param[Option[Int]]("enforceReplicationFactor", enforceReplicationFactor, None)
    .restful[PostAPICollection, CollectionInfo](body)
}