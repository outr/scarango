package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiSimpleWithinRectangle(client: HttpClient) {
  val put = new ApiSimpleWithinRectanglePut(client)
}