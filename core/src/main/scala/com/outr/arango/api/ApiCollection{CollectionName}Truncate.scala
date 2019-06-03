package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiCollection{CollectionName}Truncate(client: HttpClient) {
  val put = new ApiCollection{CollectionName}TruncatePut(client)
}