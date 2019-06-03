package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiFoxxSwaggerGet(client: HttpClient) {
  /**
  * Fetches the Swagger API description for the service at the given mount path.
  * 
  * The response body will be an OpenAPI 2.0 compatible JSON description of the service API.
  */
  def get(mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .params("mount" -> mount.toString)
    .call[ArangoResponse]
}