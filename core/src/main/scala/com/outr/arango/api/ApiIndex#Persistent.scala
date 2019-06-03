package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiIndex#Persistent(client: HttpClient) {
  val post = new ApiIndex#PersistentPost(client)
}