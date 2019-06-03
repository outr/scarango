package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminEcho(client: HttpClient) {
  val post = new AdminEchoPost(client)
}