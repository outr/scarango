package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiCollection{CollectionName}Properties(client: HttpClient) {
  val get = new ApiCollection{CollectionName}PropertiesGet(client)
  val put = new ApiCollection{CollectionName}PropertiesPut(client)
}