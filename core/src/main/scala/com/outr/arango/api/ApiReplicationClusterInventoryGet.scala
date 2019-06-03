package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiReplicationClusterInventoryGet(client: HttpClient) {
  /**
  * Returns the array of collections and indexes available on the cluster.
  * 
  * The response will be an array of JSON objects, one for each collection.
  * Each collection containscontains exactly two keys "parameters" and
  * "indexes". This
  * information comes from Plan/Collections/{DB-Name}/* in the agency,
  * just that the *indexes* attribute there is relocated to adjust it to
  * the data format of arangodump.
  */
  def get(includeSystem: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .param[Option[Boolean]]("includeSystem", includeSystem, None)
    .call[ArangoResponse]
}