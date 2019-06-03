package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiJob{JobId}(client: HttpClient) {
  val get = new ApiJob{JobId}Get(client)
  val put = new ApiJob{JobId}Put(client)
  val cancel = new ApiJob{JobId}Cancel(client)
}