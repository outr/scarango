package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object AdminTime {
  /**
  * The call returns an object with the attribute *time*. This contains the
  * current system time as a Unix timestamp with microsecond precision.
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * Time was returned successfully.
  * 
  * - **code**: the HTTP status code
  * - **time**: The current system time as a Unix timestamp with microsecond precision of the server
  * - **error**: boolean flag to indicate whether an error occurred (*false* in this case)
  */
  def get(client: HttpClient)(implicit ec: ExecutionContext): Future[GetAdminTimeRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_admin/time", append = true) 
    .call[GetAdminTimeRc200]
}