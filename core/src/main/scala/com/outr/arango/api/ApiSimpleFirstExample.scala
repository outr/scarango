package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiSimpleFirstExample(client: HttpClient) {
  val put = new ApiSimpleFirstExamplePut(client)
}