package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiUser{User}Database{Dbname}(client: HttpClient) {
  val delete = new ApiUser{User}Database{Dbname}Delete(client)
  val put = new ApiUser{User}Database{Dbname}Put(client)
  val {Collection} = new ApiUser{User}Database{Dbname}{Collection}(client)
}