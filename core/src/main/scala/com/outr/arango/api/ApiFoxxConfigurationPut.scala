package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiFoxxConfigurationPut(client: HttpClient) {
  /**
  * Replaces the given service's configuration completely.
  * 
  * Returns an object mapping all configuration option names to their new values.
  */
  def put(body: IoCirceJson, mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .params("mount" -> mount.toString)
    .restful[IoCirceJson, ArangoResponse](body)
}