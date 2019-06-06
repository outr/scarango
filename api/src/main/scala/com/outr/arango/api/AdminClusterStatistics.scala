package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
object AdminClusterStatistics {
  /**
  * Queries the statistics of the given DBserver
  */
  def get(client: HttpClient, DBserver: String): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_admin/clusterStatistics", append = true) 
    .params("DBserver" -> DBserver.toString)
    .call[Json]
}