package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class AdminDatabaseTargetVersionGet(client: HttpClient) {
  /**
  * Returns the database version that this server requires.
  * The version is returned in the *version* attribute of the result.
  */
  def get(): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .call[ArangoResponse]
}