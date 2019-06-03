package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiAqlfunction(client: HttpClient) {
  val get = new ApiAqlfunctionGet(client)
  val post = new ApiAqlfunctionPost(client)
  val {Name} = new ApiAqlfunction{Name}(client)
}