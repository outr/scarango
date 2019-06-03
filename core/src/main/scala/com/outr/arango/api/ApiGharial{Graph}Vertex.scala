package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiGharial{Graph}Vertex(client: HttpClient) {
  val get = new ApiGharial{Graph}VertexGet(client)
  val post = new ApiGharial{Graph}VertexPost(client)
  val {Collection} = new ApiGharial{Graph}Vertex{Collection}(client)
}