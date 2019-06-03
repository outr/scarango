package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiCollection{CollectionName}Count(client: HttpClient) {
  val get = new ApiCollection{CollectionName}CountGet(client)
}