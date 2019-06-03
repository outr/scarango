package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiIndex{IndexHandle}(client: HttpClient) {
  val delete = new ApiIndex{IndexHandle}Delete(client)
  val get = new ApiIndex{IndexHandle}Get(client)
}