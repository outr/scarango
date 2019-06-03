package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiSimpleAny(client: HttpClient) {
  val put = new ApiSimpleAnyPut(client)
}