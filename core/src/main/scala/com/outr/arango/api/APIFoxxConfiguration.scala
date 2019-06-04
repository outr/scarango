package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APIFoxxConfiguration(client: HttpClient) {
  /**
  * Fetches the current configuration for the service at the given mount path.
  * 
  * Returns an object mapping the configuration option names to their definitions
  * including a human-friendly *title* and the *current* value (if any).
  */
  def get(mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_api/foxx/configuration".withArguments(Map()))
    .params("mount" -> mount.toString)
    .call[ArangoResponse]

  /**
  * Replaces the given service's configuration.
  * 
  * Returns an object mapping all configuration option names to their new values.
  */
  def patch(body: IoCirceJson, mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Patch)
    .path(path"/_db/_system/_api/foxx/configuration".withArguments(Map()))
    .params("mount" -> mount.toString)
    .restful[IoCirceJson, ArangoResponse](body)

  /**
  * Replaces the given service's configuration completely.
  * 
  * Returns an object mapping all configuration option names to their new values.
  */
  def put(body: IoCirceJson, mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .path(path"/_db/_system/_api/foxx/configuration".withArguments(Map()))
    .params("mount" -> mount.toString)
    .restful[IoCirceJson, ArangoResponse](body)
}