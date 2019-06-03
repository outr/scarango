package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiSimpleReplaceByExample(client: HttpClient) {
  val put = new ApiSimpleReplaceByExamplePut(client)
}