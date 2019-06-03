package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiWalRange(client: HttpClient) {
  val get = new ApiWalRangeGet(client)
}