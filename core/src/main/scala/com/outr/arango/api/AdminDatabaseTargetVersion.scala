package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class AdminDatabaseTargetVersion(client: HttpClient) {
  /**
  * Returns the database version that this server requires.
  * The version is returned in the *version* attribute of the result.
  */
  def get(): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_admin/database/target-version".withArguments(Map()))
    .call[ArangoResponse]
}