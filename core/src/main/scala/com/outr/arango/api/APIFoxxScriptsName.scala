package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APIFoxxScriptsName(client: HttpClient) {
  /**
  * Runs the given script for the service at the given mount path.
  * 
  * Returns the exports of the script, if any.
  */
  def post(body: Option[IoCirceJson] = None, name: String, mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Post)
    .path(path"/_db/_system/_api/foxx/scripts/{name}".withArguments(Map("name" -> name)))
    .params("mount" -> mount.toString)
    .restful[Option[IoCirceJson] = None, ArangoResponse](body)
}