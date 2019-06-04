package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class AdminShutdown(client: HttpClient) {
  /**
  * This call initiates a clean shutdown sequence. Requires administrive privileges
  */
  def delete(): Future[ArangoResponse] = client
    .method(HttpMethod.Delete)
    .path(path"/_db/_system/_admin/shutdown".withArguments(Map()))
    .call[ArangoResponse]
}