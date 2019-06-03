package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiUser(client: HttpClient) {
  val post = new ApiUserPost(client)
  val get = new ApiUserGet(client)
  val {User} = new ApiUser{User}(client)
}