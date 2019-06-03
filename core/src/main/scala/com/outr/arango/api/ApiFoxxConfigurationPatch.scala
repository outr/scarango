package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiFoxxConfigurationPatch(client: HttpClient) {
  /**
  * Replaces the given service's configuration.
  * 
  * Returns an object mapping all configuration option names to their new values.
  */
  def patch(body: IoCirceJson, mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Patch)
    .params("mount" -> mount.toString)
    .restful[IoCirceJson, ArangoResponse](body)
}