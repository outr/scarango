package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiCollection{CollectionName}ResponsibleShard(client: HttpClient) {
  val put = new ApiCollection{CollectionName}ResponsibleShardPut(client)
}