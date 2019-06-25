package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object AdminClusterStatistics {

  def get(client: HttpClient, DBserver: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_admin/clusterStatistics", append = true) 
    .params("DBserver" -> DBserver.toString)
    .call[Json]
}