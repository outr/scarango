package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiEndpoint(client: HttpClient) {
  val get = new ApiEndpointGet(client)
}