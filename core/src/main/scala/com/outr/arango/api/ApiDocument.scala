package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiDocument(client: HttpClient) {
  val {Collection} = new ApiDocument{Collection}(client)
  val {DocumentHandle} = new ApiDocument{DocumentHandle}(client)
}