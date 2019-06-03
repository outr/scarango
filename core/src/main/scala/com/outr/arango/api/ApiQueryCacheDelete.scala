package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiQueryCacheDelete(client: HttpClient) {
  /**
  * clears the query results cache for the current database
  */
  def delete(): Future[ArangoResponse] = client
    .method(HttpMethod.Delete)
    .call[ArangoResponse]
}