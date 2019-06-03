package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiQuerySlowDelete(client: HttpClient) {
  /**
  * Clears the list of slow AQL queries
  */
  def delete(): Future[ArangoResponse] = client
    .method(HttpMethod.Delete)
    .call[ArangoResponse]
}