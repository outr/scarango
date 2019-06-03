package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiQuery{QueryId}(client: HttpClient) {
  val delete = new ApiQuery{QueryId}Delete(client)
}