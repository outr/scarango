package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiIndex#Fulltext(client: HttpClient) {
  val post = new ApiIndex#FulltextPost(client)
}