package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiTraversal(client: HttpClient) {
  val post = new ApiTraversalPost(client)
}