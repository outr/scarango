package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiReplicationSync(client: HttpClient) {
  val put = new ApiReplicationSyncPut(client)
}