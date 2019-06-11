package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIFoxxSwagger {
  /**
  * Fetches the Swagger API description for the service at the given mount path.
  * 
  * The response body will be an OpenAPI 2.0 compatible JSON description of the service API.
  */
  def get(client: HttpClient, mount: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/foxx/swagger", append = true) 
    .params("mount" -> mount.toString)
    .call[Json]
}