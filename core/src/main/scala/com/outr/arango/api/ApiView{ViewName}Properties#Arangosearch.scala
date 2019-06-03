package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiView{ViewName}Properties#Arangosearch(client: HttpClient) {
  val patch = new ApiView{ViewName}Properties#ArangosearchPatch(client)
  val put = new ApiView{ViewName}Properties#ArangosearchPut(client)
}