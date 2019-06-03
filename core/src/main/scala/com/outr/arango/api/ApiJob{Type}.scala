package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiJob{Type}(client: HttpClient) {
  val delete = new ApiJob{Type}Delete(client)
  val get = new ApiJob{Type}Get(client)
}