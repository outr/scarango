package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiView#ArangoSearch(client: HttpClient) {
  val post = new ApiView#ArangoSearchPost(client)
}