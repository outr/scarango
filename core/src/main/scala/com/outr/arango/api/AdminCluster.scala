package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminCluster(client: HttpClient) {
  val health = new AdminClusterHealth(client)
  val maintenance = new AdminClusterMaintenance(client)
}