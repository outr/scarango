package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiBatch(client: HttpClient) {
  val post = new ApiBatchPost(client)
}