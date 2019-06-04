package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIFoxxCommit(client: HttpClient) {
  /**
  * Commits the local service state of the coordinator to the database.
  * 
  * This can be used to resolve service conflicts between coordinators that can not be fixed automatically due to missing data.
  */
  def post(replace: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Post)
    .path(path"/_db/_system/_api/foxx/commit".withArguments(Map()))
    .param[Option[Boolean]]("replace", replace, None)
    .call[ArangoResponse]
}