package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminStatisticsDescription(client: HttpClient) {
  val get = new AdminStatisticsDescriptionGet(client)
}