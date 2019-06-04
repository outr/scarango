package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APIFoxxReadme(client: HttpClient) {
  /**
  * Fetches the service's README or README.md file's contents if any.
  */
  def get(mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_api/foxx/readme".withArguments(Map()))
    .params("mount" -> mount.toString)
    .call[ArangoResponse]
}