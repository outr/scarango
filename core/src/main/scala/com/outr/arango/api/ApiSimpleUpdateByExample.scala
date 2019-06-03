package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiSimpleUpdateByExample(client: HttpClient) {
  val put = new ApiSimpleUpdateByExamplePut(client)
}