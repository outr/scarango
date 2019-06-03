package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiTransaction(client: HttpClient) {
  val post = new ApiTransactionPost(client)
}