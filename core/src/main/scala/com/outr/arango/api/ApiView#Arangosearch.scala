package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiView#Arangosearch(client: HttpClient) {
  val post = new ApiView#ArangosearchPost(client)
}