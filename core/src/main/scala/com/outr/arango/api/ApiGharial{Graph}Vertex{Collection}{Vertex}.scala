package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiGharial{Graph}Vertex{Collection}{Vertex}(client: HttpClient) {
  val delete = new ApiGharial{Graph}Vertex{Collection}{Vertex}Delete(client)
  val get = new ApiGharial{Graph}Vertex{Collection}{Vertex}Get(client)
  val patch = new ApiGharial{Graph}Vertex{Collection}{Vertex}Patch(client)
  val put = new ApiGharial{Graph}Vertex{Collection}{Vertex}Put(client)
}