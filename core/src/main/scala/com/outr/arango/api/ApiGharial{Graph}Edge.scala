package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiGharial{Graph}Edge(client: HttpClient) {
  val get = new ApiGharial{Graph}EdgeGet(client)
  val post = new ApiGharial{Graph}EdgePost(client)
  val {Collection} = new ApiGharial{Graph}Edge{Collection}(client)
  val {Definition} = new ApiGharial{Graph}Edge{Definition}(client)
}