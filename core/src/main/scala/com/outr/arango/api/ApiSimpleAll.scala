package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiSimpleAll(client: HttpClient) {
  val put = new ApiSimpleAllPut(client)
}