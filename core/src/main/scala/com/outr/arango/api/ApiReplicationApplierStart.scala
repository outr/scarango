package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiReplicationApplierStart(client: HttpClient) {
  val put = new ApiReplicationApplierStartPut(client)
}