package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiFoxxGet(client: HttpClient) {
  /**
  * Fetches a list of services installed in the current database.
  * 
  * Returns a list of objects with the following attributes:
  * 
  * - *mount*: the mount path of the service
  * - *development*: *true* if the service is running in development mode
  * - *legacy*: *true* if the service is running in 2.8 legacy compatibility mode
  * - *provides*: the service manifest's *provides* value or an empty object
  * 
  * Additionally the object may contain the following attributes if they have been set on the manifest:
  * 
  * - *name*: a string identifying the service type
  * - *version*: a semver-compatible version string
  */
  def get(excludeSystem: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .param[Option[Boolean]]("excludeSystem", excludeSystem, None)
    .call[ArangoResponse]
}