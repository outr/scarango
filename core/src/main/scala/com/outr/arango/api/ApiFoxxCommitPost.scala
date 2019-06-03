package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiFoxxCommitPost(client: HttpClient) {
  /**
  * Commits the local service state of the coordinator to the database.
  * 
  * This can be used to resolve service conflicts between coordinators that can not be fixed automatically due to missing data.
  */
  def post(replace: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Post)
    .param[Option[Boolean]]("replace", replace, None)
    .call[ArangoResponse]
}