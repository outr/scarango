package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiDocument{Collection}(client: HttpClient) {
  val delete = new ApiDocument{Collection}Delete(client)
  val patch = new ApiDocument{Collection}Patch(client)
  val post = new ApiDocument{Collection}Post(client)
  val put = new ApiDocument{Collection}Put(client)
}