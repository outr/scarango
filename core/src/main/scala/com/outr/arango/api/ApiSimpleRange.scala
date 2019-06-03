package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiSimpleRange(client: HttpClient) {
  val put = new ApiSimpleRangePut(client)
}