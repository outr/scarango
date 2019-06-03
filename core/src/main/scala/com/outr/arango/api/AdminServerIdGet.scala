package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class AdminServerIdGet(client: HttpClient) {
  /**
  * Returns the id of a server in a cluster. The request will fail if the
  * server is not running in cluster mode.
  */
  def get(): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .call[ArangoResponse]
}