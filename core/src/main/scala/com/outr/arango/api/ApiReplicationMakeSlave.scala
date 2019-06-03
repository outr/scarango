package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiReplicationMakeSlave(client: HttpClient) {
  val put = new ApiReplicationMakeSlavePut(client)
}