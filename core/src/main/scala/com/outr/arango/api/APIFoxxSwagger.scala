package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIFoxxSwagger(client: HttpClient) {
  /**
  * Fetches the Swagger API description for the service at the given mount path.
  * 
  * The response body will be an OpenAPI 2.0 compatible JSON description of the service API.
  */
  def get(mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_api/foxx/swagger".withArguments(Map()))
    .params("mount" -> mount.toString)
    .call[ArangoResponse]
}