package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiReplicationInventory(client: HttpClient) {
  val get = new ApiReplicationInventoryGet(client)
}