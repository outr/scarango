package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiSimpleByExample(client: HttpClient) {
  val put = new ApiSimpleByExamplePut(client)
}