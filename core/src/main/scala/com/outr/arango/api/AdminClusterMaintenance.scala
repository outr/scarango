package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminClusterMaintenance(client: HttpClient) {
  val put = new AdminClusterMaintenancePut(client)
}