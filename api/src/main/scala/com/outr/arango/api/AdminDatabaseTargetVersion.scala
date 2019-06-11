package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object AdminDatabaseTargetVersion {
  /**
  * Returns the database version that this server requires.
  * The version is returned in the *version* attribute of the result.
  */
  def get(client: HttpClient)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_admin/database/target-version", append = true) 
    .call[Json]
}