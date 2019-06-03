package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiQuery(client: HttpClient) {
  val post = new ApiQueryPost(client)
  val {QueryId} = new ApiQuery{QueryId}(client)
  val slow = new ApiQuerySlow(client)
  val properties = new ApiQueryProperties(client)
  val current = new ApiQueryCurrent(client)
}