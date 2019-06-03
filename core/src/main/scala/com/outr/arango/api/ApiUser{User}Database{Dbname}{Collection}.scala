package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiUser{User}Database{Dbname}{Collection}(client: HttpClient) {
  val delete = new ApiUser{User}Database{Dbname}{Collection}Delete(client)
  val put = new ApiUser{User}Database{Dbname}{Collection}Put(client)
}