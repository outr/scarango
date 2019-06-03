package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminStatistics(client: HttpClient) {
  val get = new AdminStatisticsGet(client)
}