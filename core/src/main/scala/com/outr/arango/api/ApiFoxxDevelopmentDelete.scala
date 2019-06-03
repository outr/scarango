package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiFoxxDevelopmentDelete(client: HttpClient) {
  /**
  * Puts the service at the given mount path into production mode.
  * 
  * When running ArangoDB in a cluster with multiple coordinators this will
  * replace the service on all other coordinators with the version on this
  * coordinator.
  */
  def delete(mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Delete)
    .params("mount" -> mount.toString)
    .call[ArangoResponse]
}