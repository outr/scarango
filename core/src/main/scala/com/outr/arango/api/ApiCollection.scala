package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiCollection(client: HttpClient) {
  val get = new ApiCollectionGet(client)
  val post = new ApiCollectionPost(client)
  val {CollectionName} = new ApiCollection{CollectionName}(client)
}