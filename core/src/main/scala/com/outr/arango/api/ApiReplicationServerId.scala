package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiReplicationServerId(client: HttpClient) {
  val get = new ApiReplicationServerIdGet(client)
}