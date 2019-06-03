package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiFoxxConfigurationGet(client: HttpClient) {
  /**
  * Fetches the current configuration for the service at the given mount path.
  * 
  * Returns an object mapping the configuration option names to their definitions
  * including a human-friendly *title* and the *current* value (if any).
  */
  def get(mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .params("mount" -> mount.toString)
    .call[ArangoResponse]
}