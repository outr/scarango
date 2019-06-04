package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIClusterEndpoints(client: HttpClient) {
  /**
  * Returns an object with an attribute `endpoints`, which contains an
  * array of objects, which each have the attribute `endpoint`, whose value
  * is a string with the endpoint description. There is an entry for each
  * coordinator in the cluster. This method only works on coordinators in
  * cluster mode. In case of an error the `error` attribute is set to
  * `true`.
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * - **endpoints**: A list of active cluster endpoints.
  *   - **endpoint**: The bind of the coordinator, like `tcp://[::1]:8530`
  * - **code**: the HTTP status code - 200
  * - **error**: boolean flag to indicate whether an error occurred (*true* in this case)
  */
  def get(): Future[GetAPIClusterEndpointsRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_api/cluster/endpoints".withArguments(Map()))
    .call[GetAPIClusterEndpointsRc200]
}