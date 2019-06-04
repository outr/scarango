package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIReplicationBatchId(client: HttpClient) {
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
    .path(path"/_db/_system/_api/replication/batch/{id}".withArguments(Map("id" -> id)))
    .call[ArangoResponse]

  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **ttl**: the time-to-live for the new batch (in seconds)
  * 
  * 
  * 
  * 
  * Extends the ttl of an existing dump batch, using the batch's id and
  * the provided ttl value.
  * 
  * If the batch's ttl can be extended successfully, the response is empty.
  * 
  * **Note**: on a coordinator, this request must have the query parameter
  * *DBserver* which must be an ID of a DBserver.
  * The very same request is forwarded synchronously to that DBserver.
  * It is an error if this attribute is not bound in the coordinator case.
  */
  def put(body: PutBatchReplication, id: String): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .path(path"/_db/_system/_api/replication/batch/{id}".withArguments(Map("id" -> id)))
    .restful[PutBatchReplication, ArangoResponse](body)
}