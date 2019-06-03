package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiIndex#Skiplist(client: HttpClient) {
  val post = new ApiIndex#SkiplistPost(client)
}