package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIFoxxDownload(client: HttpClient) {
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
    .path(path"/_db/_system/_api/foxx/download".withArguments(Map()))
    .params("mount" -> mount.toString)
    .call[ArangoResponse]
}