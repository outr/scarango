package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIQueryCache(client: HttpClient) {
  /**
  * clears the query results cache for the current database
  */
  def delete(): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/query-cache", append = true) 
    .call[Json]
}