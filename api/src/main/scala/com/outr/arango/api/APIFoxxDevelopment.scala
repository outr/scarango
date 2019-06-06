package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
object APIFoxxDevelopment {
  /**
  * Puts the service at the given mount path into production mode.
  * 
  * When running ArangoDB in a cluster with multiple coordinators this will
  * replace the service on all other coordinators with the version on this
  * coordinator.
  */
  def delete(client: HttpClient, mount: String): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/foxx/development", append = true) 
    .params("mount" -> mount.toString)
    .call[Json]

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
  def post(client: HttpClient, mount: String): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/foxx/development", append = true) 
    .params("mount" -> mount.toString)
    .call[Json]
}