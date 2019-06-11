package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIReplicationBatch {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **ttl**: the time-to-live for the new batch (in seconds)
  *    A JSON object with the batch configuration.
  * 
  * 
  * 
  * 
  * Creates a new dump batch and returns the batch's id.
  * 
  * The response is a JSON object with the following attributes:
  * 
  * - *id*: the id of the batch
  * 
  * **Note**: on a coordinator, this request must have the query parameter
  * *DBserver* which must be an ID of a DBserver.
  * The very same request is forwarded synchronously to that DBserver.
  * It is an error if this attribute is not bound in the coordinator case.
  */
  def post(client: HttpClient, body: PostBatchReplication)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/replication/batch", append = true) 
    .restful[PostBatchReplication, Json](body)
}