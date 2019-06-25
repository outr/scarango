package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIVersion {

  def get(client: HttpClient, details: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[GetAPIReturnRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_api/version", append = true) 
    .param[Option[Boolean]]("details", details, None)
    .call[GetAPIReturnRc200]
}