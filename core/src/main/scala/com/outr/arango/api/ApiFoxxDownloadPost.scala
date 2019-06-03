package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiFoxxDownloadPost(client: HttpClient) {
  /**
  * Downloads a zip bundle of the service directory.
  * 
  * When development mode is enabled, this always creates a new bundle.
  * 
  * Otherwise the bundle will represent the version of a service that
  * is installed on that ArangoDB instance.
  */
  def post(mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Post)
    .params("mount" -> mount.toString)
    .call[ArangoResponse]
}