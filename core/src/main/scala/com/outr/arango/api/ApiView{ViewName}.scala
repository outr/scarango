package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiView{ViewName}(client: HttpClient) {
  val properties#ArangoSearch = new ApiView{ViewName}Properties#ArangoSearch(client)
  val properties#Arangosearch = new ApiView{ViewName}Properties#Arangosearch(client)
  val rename = new ApiView{ViewName}Rename(client)
  val properties = new ApiView{ViewName}Properties(client)
  val delete = new ApiView{ViewName}Delete(client)
  val get = new ApiView{ViewName}Get(client)
}