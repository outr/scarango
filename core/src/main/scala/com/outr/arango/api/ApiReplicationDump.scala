package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiReplicationDump(client: HttpClient) {
  val get = new ApiReplicationDumpGet(client)
}