package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiFoxxScriptsGet(client: HttpClient) {
  /**
  * Fetches a list of the scripts defined by the service.
  * 
  * Returns an object mapping the raw script names to human-friendly names.
  */
  def get(mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .params("mount" -> mount.toString)
    .call[ArangoResponse]
}