package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiJob{JobId}Cancel(client: HttpClient) {
  val put = new ApiJob{JobId}CancelPut(client)
}