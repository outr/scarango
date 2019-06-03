package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiIndex(client: HttpClient) {
  val get = new ApiIndexGet(client)
  val {IndexHandle} = new ApiIndex{IndexHandle}(client)
}