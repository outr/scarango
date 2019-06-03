package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiReplicationApplierStop(client: HttpClient) {
  val put = new ApiReplicationApplierStopPut(client)
}