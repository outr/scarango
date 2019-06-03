package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiQueryCacheProperties(client: HttpClient) {
  val get = new ApiQueryCachePropertiesGet(client)
  val put = new ApiQueryCachePropertiesPut(client)
}