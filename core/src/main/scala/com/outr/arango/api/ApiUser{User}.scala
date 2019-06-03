package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiUser{User}(client: HttpClient) {
  val database = new ApiUser{User}Database(client)
  val delete = new ApiUser{User}Delete(client)
  val put = new ApiUser{User}Put(client)
  val get = new ApiUser{User}Get(client)
  val patch = new ApiUser{User}Patch(client)
}