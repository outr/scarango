package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APICollectionCollectionNameRecalculateCount(client: HttpClient) {
  /**
  * Recalculates the document count of a collection, if it ever becomes inconsistent.
  * 
  * It returns an object with the attributes
  * 
  * - *result*: will be *true* if recalculating the document count succeeded.
  * 
  * **Note**: this method is specific for the RocksDB storage engine
  */
  def put(collectionName: String): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/collection/{collection-name}/recalculateCount".withArguments(Map("collection-name" -> collectionName)), append = true)
    .call[Json]
}