package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiGharial{Graph}Vertex{Collection}(client: HttpClient) {
  val delete = new ApiGharial{Graph}Vertex{Collection}Delete(client)
  val post = new ApiGharial{Graph}Vertex{Collection}Post(client)
  val {Vertex} = new ApiGharial{Graph}Vertex{Collection}{Vertex}(client)
}