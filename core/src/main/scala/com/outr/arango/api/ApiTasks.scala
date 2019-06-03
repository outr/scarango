package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiTasks(client: HttpClient) {
  val post = new ApiTasksPost(client)
  val get = new ApiTasksGet(client)
  val {Id} = new ApiTasks{Id}(client)
}