package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiCollection{CollectionName}Load(client: HttpClient) {
  val put = new ApiCollection{CollectionName}LoadPut(client)
}