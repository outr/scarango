package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiCollection{CollectionName}Figures(client: HttpClient) {
  val get = new ApiCollection{CollectionName}FiguresGet(client)
}