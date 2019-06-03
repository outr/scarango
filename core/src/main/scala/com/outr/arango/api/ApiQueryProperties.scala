package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiQueryProperties(client: HttpClient) {
  val get = new ApiQueryPropertiesGet(client)
  val put = new ApiQueryPropertiesPut(client)
}