package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiIndex#Hash(client: HttpClient) {
  val post = new ApiIndex#HashPost(client)
}