package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiReplicationLoggerFirstTick(client: HttpClient) {
  val get = new ApiReplicationLoggerFirstTickGet(client)
}