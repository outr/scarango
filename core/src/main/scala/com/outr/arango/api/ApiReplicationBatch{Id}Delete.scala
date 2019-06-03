package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiReplicationBatch{Id}Delete(client: HttpClient) {
  /**
  * Deletes the existing dump batch, allowing compaction and cleanup to resume.
  * 
  * **Note**: on a coordinator, this request must have the query parameter
  * *DBserver* which must be an ID of a DBserver.
  * The very same request is forwarded synchronously to that DBserver.
  * It is an error if this attribute is not bound in the coordinator case.
  */
  def delete(id: String): Future[ArangoResponse] = client
    .method(HttpMethod.Delete)
    .params("id" -> id.toString)
    .call[ArangoResponse]
}