package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiFoxxDependenciesGet(client: HttpClient) {
  /**
  * Fetches the current dependencies for service at the given mount path.
  * 
  * Returns an object mapping the dependency names to their definitions
  * including a human-friendly *title* and the *current* mount path (if any).
  */
  def get(mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .params("mount" -> mount.toString)
    .call[ArangoResponse]
}