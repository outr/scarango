package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiView{ViewName}Properties#ArangoSearch(client: HttpClient) {
  val patch = new ApiView{ViewName}Properties#ArangoSearchPatch(client)
  val put = new ApiView{ViewName}Properties#ArangoSearchPut(client)
}