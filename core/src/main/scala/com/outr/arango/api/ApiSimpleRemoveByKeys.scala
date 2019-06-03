package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiSimpleRemoveByKeys(client: HttpClient) {
  val put = new ApiSimpleRemoveByKeysPut(client)
}