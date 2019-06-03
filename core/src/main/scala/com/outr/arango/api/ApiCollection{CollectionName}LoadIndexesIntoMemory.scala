package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiCollection{CollectionName}LoadIndexesIntoMemory(client: HttpClient) {
  val put = new ApiCollection{CollectionName}LoadIndexesIntoMemoryPut(client)
}