package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiFoxxServiceGet(client: HttpClient) {
  /**
  * Fetches detailed information for the service at the given mount path.
  * 
  * Returns an object with the following attributes:
  * 
  * - *mount*: the mount path of the service
  * - *path*: the local file system path of the service
  * - *development*: *true* if the service is running in development mode
  * - *legacy*: *true* if the service is running in 2.8 legacy compatibility mode
  * - *manifest*: the normalized JSON manifest of the service
  * 
  * Additionally the object may contain the following attributes if they have been set on the manifest:
  * 
  * - *name*: a string identifying the service type
  * - *version*: a semver-compatible version string
  */
  def get(mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .params("mount" -> mount.toString)
    .call[ArangoResponse]
}