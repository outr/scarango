package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminClusterStatistics(client: HttpClient) {
  val get = new AdminClusterStatisticsGet(client)
}