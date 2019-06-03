package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiFoxxReadmeGet(client: HttpClient) {
  /**
  * Fetches the service's README or README.md file's contents if any.
  */
  def get(mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .params("mount" -> mount.toString)
    .call[ArangoResponse]
}