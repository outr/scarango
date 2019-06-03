package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiReplicationBatch{Id}Put(client: HttpClient) {
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
    .params("id" -> id.toString)
    .restful[PutBatchReplication, ArangoResponse](body)
}