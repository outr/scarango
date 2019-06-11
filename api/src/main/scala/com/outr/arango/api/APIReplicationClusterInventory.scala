package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIReplicationClusterInventory {
  /**
  * Returns the array of collections and indexes available on the cluster.
  * 
  * The response will be an array of JSON objects, one for each collection.
  * Each collection containscontains exactly two keys "parameters" and
  * "indexes". This
  * information comes from Plan/Collections/{DB-Name}/{@literal *} in the agency,
  * just that the *indexes* attribute there is relocated to adjust it to
  * the data format of arangodump.
  */
  def get(client: HttpClient, includeSystem: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/replication/clusterInventory", append = true) 
    .param[Option[Boolean]]("includeSystem", includeSystem, None)
    .call[Json]
}