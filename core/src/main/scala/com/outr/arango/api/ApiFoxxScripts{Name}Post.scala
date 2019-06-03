package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiFoxxScripts{Name}Post(client: HttpClient) {
  /**
  * Runs the given script for the service at the given mount path.
  * 
  * Returns the exports of the script, if any.
  */
  def post(body: Option[IoCirceJson] = None, name: String, mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Post)
    .params("name" -> name.toString)
    .params("mount" -> mount.toString)
    .restful[Option[IoCirceJson] = None, ArangoResponse](body)
}