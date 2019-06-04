package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APICollectionCollectionNameResponsibleShard(client: HttpClient) {
  /**
  * Returns the ID of the shard that is responsible for the given document
  * (if the document exists) or that would be responsible if such document
  * existed.
  * 
  * The response is a JSON object with a *shardId* attribute, which will 
  * contain the ID of the responsible shard.
  * 
  * **Note** : This method is only available in a cluster coordinator.
  */
  def put(collectionName: String): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .path(path"/_db/_system/_api/collection/{collection-name}/responsibleShard".withArguments(Map("collection-name" -> collectionName)))
    .call[ArangoResponse]
}