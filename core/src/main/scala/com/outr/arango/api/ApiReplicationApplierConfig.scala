package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiReplicationApplierConfig(client: HttpClient) {
  val get = new ApiReplicationApplierConfigGet(client)
  val put = new ApiReplicationApplierConfigPut(client)
}