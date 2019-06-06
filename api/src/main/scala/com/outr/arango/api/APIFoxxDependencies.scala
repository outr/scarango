package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
object APIFoxxDependencies {
  /**
  * Fetches the current dependencies for service at the given mount path.
  * 
  * Returns an object mapping the dependency names to their definitions
  * including a human-friendly *title* and the *current* mount path (if any).
  */
  def get(client: HttpClient, mount: String): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/foxx/dependencies", append = true) 
    .params("mount" -> mount.toString)
    .call[Json]

  /**
  * Replaces the given service's dependencies.
  * 
  * Returns an object mapping all dependency names to their new mount paths.
  */
  def patch(client: HttpClient, body: Json, mount: String): Future[Json] = client
    .method(HttpMethod.Patch)
    .path(path"/_api/foxx/dependencies", append = true) 
    .params("mount" -> mount.toString)
    .restful[Json, Json](body)

  /**
  * Replaces the given service's dependencies completely.
  * 
  * Returns an object mapping all dependency names to their new mount paths.
  */
  def put(client: HttpClient, body: Json, mount: String): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/foxx/dependencies", append = true) 
    .params("mount" -> mount.toString)
    .restful[Json, Json](body)
}