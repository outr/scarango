package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiFoxxDependenciesPut(client: HttpClient) {
  /**
  * Replaces the given service's dependencies completely.
  * 
  * Returns an object mapping all dependency names to their new mount paths.
  */
  def put(body: IoCirceJson, mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .params("mount" -> mount.toString)
    .restful[IoCirceJson, ArangoResponse](body)
}