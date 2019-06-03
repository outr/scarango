package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiReplicationBatch{Id}(client: HttpClient) {
  val delete = new ApiReplicationBatch{Id}Delete(client)
  val put = new ApiReplicationBatch{Id}Put(client)
}