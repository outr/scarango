package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiSimpleAllKeys(client: HttpClient) {
  val put = new ApiSimpleAllKeysPut(client)
}