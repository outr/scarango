package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiFoxxServiceDelete(client: HttpClient) {
  /**
  * Removes the service at the given mount path from the database and file system.
  * 
  * Returns an empty response on success.
  */
  def delete(mount: String, teardown: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Delete)
    .params("mount" -> mount.toString)
    .param[Option[Boolean]]("teardown", teardown, None)
    .call[ArangoResponse]
}