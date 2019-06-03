package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiView(client: HttpClient) {
  val get = new ApiViewGet(client)
  val {ViewName} = new ApiView{ViewName}(client)
}