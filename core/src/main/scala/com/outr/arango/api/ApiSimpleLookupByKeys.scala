package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiSimpleLookupByKeys(client: HttpClient) {
  val put = new ApiSimpleLookupByKeysPut(client)
}