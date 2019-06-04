package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class AdminServerAvailability(client: HttpClient) {
  /**
  * Return availability information about a server.
  * 
  * This is a public API so it does *not* require authentication. It is meant to be
  * used only in the context of server monitoring only.
  */
  def get(): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_admin/server/availability".withArguments(Map()))
    .call[ArangoResponse]
}