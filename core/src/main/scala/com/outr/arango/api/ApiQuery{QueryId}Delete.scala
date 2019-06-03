package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiQuery{QueryId}Delete(client: HttpClient) {
  /**
  * Kills a running query. The query will be terminated at the next cancelation
  * point.
  */
  def delete(queryId: String): Future[ArangoResponse] = client
    .method(HttpMethod.Delete)
    .params("query-id" -> query-id.toString)
    .call[ArangoResponse]
}