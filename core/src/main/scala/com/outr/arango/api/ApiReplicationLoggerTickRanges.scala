package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiReplicationLoggerTickRanges(client: HttpClient) {
  val get = new ApiReplicationLoggerTickRangesGet(client)
}