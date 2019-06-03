package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminExecute(client: HttpClient) {
  val post = new AdminExecutePost(client)
}