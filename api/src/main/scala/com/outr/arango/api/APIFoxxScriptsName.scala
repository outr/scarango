package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
object APIFoxxScriptsName {
  /**
  * Runs the given script for the service at the given mount path.
  * 
  * Returns the exports of the script, if any.
  */
  def post(client: HttpClient, body: Json, name: String, mount: String): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/foxx/scripts/{name}".withArguments(Map("name" -> name)), append = true)
    .params("mount" -> mount.toString)
    .restful[Json, Json](body)
}