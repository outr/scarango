package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiIndex#Ttl(client: HttpClient) {
  val post = new ApiIndex#TtlPost(client)
}