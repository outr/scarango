package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIFoxxDependencies(client: HttpClient) {
  /**
  * Fetches the current dependencies for service at the given mount path.
  * 
  * Returns an object mapping the dependency names to their definitions
  * including a human-friendly *title* and the *current* mount path (if any).
  */
  def get(mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_api/foxx/dependencies".withArguments(Map()))
    .params("mount" -> mount.toString)
    .call[ArangoResponse]

  /**
  * Replaces the given service's dependencies.
  * 
  * Returns an object mapping all dependency names to their new mount paths.
  */
  def patch(body: Json, mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Patch)
    .path(path"/_db/_system/_api/foxx/dependencies".withArguments(Map()))
    .params("mount" -> mount.toString)
    .restful[Json, ArangoResponse](body)

  /**
  * Replaces the given service's dependencies completely.
  * 
  * Returns an object mapping all dependency names to their new mount paths.
  */
  def put(body: Json, mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .path(path"/_db/_system/_api/foxx/dependencies".withArguments(Map()))
    .params("mount" -> mount.toString)
    .restful[Json, ArangoResponse](body)
}