package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiTasks{Id}(client: HttpClient) {
  val delete = new ApiTasks{Id}Delete(client)
  val get = new ApiTasks{Id}Get(client)
  val put = new ApiTasks{Id}Put(client)
}