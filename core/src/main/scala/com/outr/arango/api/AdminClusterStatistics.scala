package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class AdminClusterStatistics(client: HttpClient) {
  /**
  * Queries the statistics of the given DBserver
  */
  def get(DBserver: String): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_admin/clusterStatistics".withArguments(Map()))
    .params("DBserver" -> DBserver.toString)
    .call[ArangoResponse]
}