package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class AdminClusterStatisticsGet(client: HttpClient) {
  /**
  * Queries the statistics of the given DBserver
  */
  def get(DBserver: String): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .params("DBserver" -> DBserver.toString)
    .call[ArangoResponse]
}