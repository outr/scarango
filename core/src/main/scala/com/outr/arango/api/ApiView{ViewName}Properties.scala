package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiView{ViewName}Properties(client: HttpClient) {
  val get = new ApiView{ViewName}PropertiesGet(client)
}