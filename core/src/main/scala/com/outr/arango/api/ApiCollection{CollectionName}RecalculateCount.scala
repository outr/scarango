package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiCollection{CollectionName}RecalculateCount(client: HttpClient) {
  val put = new ApiCollection{CollectionName}RecalculateCountPut(client)
}