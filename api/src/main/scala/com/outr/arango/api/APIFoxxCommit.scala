package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIFoxxCommit {
  /**
  * Commits the local service state of the coordinator to the database.
  * 
  * This can be used to resolve service conflicts between coordinators that can not be fixed automatically due to missing data.
  */
  def post(client: HttpClient, replace: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/foxx/commit", append = true) 
    .param[Option[Boolean]]("replace", replace, None)
    .call[Json]
}