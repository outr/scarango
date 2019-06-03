package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiReplicationBatch(client: HttpClient) {
  val post = new ApiReplicationBatchPost(client)
  val {Id} = new ApiReplicationBatch{Id}(client)
}