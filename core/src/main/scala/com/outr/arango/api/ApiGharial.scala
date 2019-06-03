package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiGharial(client: HttpClient) {
  val get = new ApiGharialGet(client)
  val post = new ApiGharialPost(client)
  val {Graph} = new ApiGharial{Graph}(client)
}