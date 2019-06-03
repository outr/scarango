package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiSimpleRemoveByExample(client: HttpClient) {
  val put = new ApiSimpleRemoveByExamplePut(client)
}