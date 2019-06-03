package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiSimpleNear(client: HttpClient) {
  val put = new ApiSimpleNearPut(client)
}