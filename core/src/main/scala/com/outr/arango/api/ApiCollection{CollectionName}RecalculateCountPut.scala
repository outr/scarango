package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiCollection{CollectionName}RecalculateCountPut(client: HttpClient) {
  /**
  * Recalculates the document count of a collection, if it ever becomes inconsistent.
  * 
  * It returns an object with the attributes
  * 
  * - *result*: will be *true* if recalculating the document count succeeded.
  * 
  * **Note**: this method is specific for the RocksDB storage engine
  */
  def put(collectionName: String): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .params("collection-name" -> collection-name.toString)
    .call[ArangoResponse]
}