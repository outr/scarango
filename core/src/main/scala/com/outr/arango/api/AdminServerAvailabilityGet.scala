package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class AdminServerAvailabilityGet(client: HttpClient) {
  /**
  * Return availability information about a server.
  * 
  * This is a public API so it does *not* require authentication. It is meant to be
  * used only in the context of server monitoring only.
  */
  def get(): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .call[ArangoResponse]
}