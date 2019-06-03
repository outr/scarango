package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiFoxxDependenciesPatch(client: HttpClient) {
  /**
  * Replaces the given service's dependencies.
  * 
  * Returns an object mapping all dependency names to their new mount paths.
  */
  def patch(body: IoCirceJson, mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Patch)
    .params("mount" -> mount.toString)
    .restful[IoCirceJson, ArangoResponse](body)
}