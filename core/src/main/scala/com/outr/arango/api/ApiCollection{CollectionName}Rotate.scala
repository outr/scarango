package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiCollection{CollectionName}Rotate(client: HttpClient) {
  val put = new ApiCollection{CollectionName}RotatePut(client)
}