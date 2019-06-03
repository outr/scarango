package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class AdminRoutingReloadPost(client: HttpClient) {
  /**
  * Reloads the routing information from the collection *routing*.
  */
  def post(): Future[ArangoResponse] = client
    .method(HttpMethod.Post)
    .call[ArangoResponse]
}