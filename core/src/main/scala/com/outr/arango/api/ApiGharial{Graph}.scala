package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiGharial{Graph}(client: HttpClient) {
  val delete = new ApiGharial{Graph}Delete(client)
  val get = new ApiGharial{Graph}Get(client)
  val edge = new ApiGharial{Graph}Edge(client)
  val vertex = new ApiGharial{Graph}Vertex(client)
}