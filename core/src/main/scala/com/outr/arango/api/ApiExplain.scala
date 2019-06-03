package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiExplain(client: HttpClient) {
  val post = new ApiExplainPost(client)
}