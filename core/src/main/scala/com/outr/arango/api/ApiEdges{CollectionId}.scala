package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiEdges{CollectionId}(client: HttpClient) {
  val get = new ApiEdges{CollectionId}Get(client)
}