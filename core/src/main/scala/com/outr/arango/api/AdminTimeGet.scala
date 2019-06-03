package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class AdminTimeGet(client: HttpClient) {
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
  def get(): Future[GetAdminTimeRc200] = client
    .method(HttpMethod.Get)
    .call[GetAdminTimeRc200]
}