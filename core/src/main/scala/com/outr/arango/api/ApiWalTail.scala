package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiWalTail(client: HttpClient) {
  val get = new ApiWalTailGet(client)
}