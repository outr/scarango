package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class AdminServerId(client: HttpClient) {
  /**
  * Returns the id of a server in a cluster. The request will fail if the
  * server is not running in cluster mode.
  */
  def get(): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_admin/server/id".withArguments(Map()))
    .call[ArangoResponse]
}