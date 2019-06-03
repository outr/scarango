package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiCollection{CollectionName}Unload(client: HttpClient) {
  val put = new ApiCollection{CollectionName}UnloadPut(client)
}