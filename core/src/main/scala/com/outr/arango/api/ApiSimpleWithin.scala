package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiSimpleWithin(client: HttpClient) {
  val put = new ApiSimpleWithinPut(client)
}