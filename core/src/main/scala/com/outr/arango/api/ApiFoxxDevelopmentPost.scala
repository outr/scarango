package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiFoxxDevelopmentPost(client: HttpClient) {
  /**
  * Puts the service into development mode.
  * 
  * While the service is running in development mode the service will be reloaded
  * from the filesystem and its setup script (if any) will be re-executed every
  * time the service handles a request.
  * 
  * When running ArangoDB in a cluster with multiple coordinators note that changes
  * to the filesystem on one coordinator will not be reflected across the other
  * coordinators. This means you should treat your coordinators as inconsistent
  * as long as any service is running in development mode.
  */
  def post(mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Post)
    .params("mount" -> mount.toString)
    .call[ArangoResponse]
}