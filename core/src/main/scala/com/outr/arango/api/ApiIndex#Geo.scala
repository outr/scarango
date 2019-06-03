package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiIndex#Geo(client: HttpClient) {
  val post = new ApiIndex#GeoPost(client)
}