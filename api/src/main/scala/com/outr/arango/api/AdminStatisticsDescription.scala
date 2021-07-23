package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object AdminStatisticsDescription {

  def get(client: HttpClient)(implicit ec: ExecutionContext): Future[GetAdminStatisticsDescriptionRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_admin/statistics-description", append = true) 
    .call[GetAdminStatisticsDescriptionRc200]
}