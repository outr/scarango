package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIReplicationClusterInventory {

  def get(client: HttpClient, includeSystem: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/replication/clusterInventory", append = true) 
    .param[Option[Boolean]]("includeSystem", includeSystem, None)
    .call[Json]
}