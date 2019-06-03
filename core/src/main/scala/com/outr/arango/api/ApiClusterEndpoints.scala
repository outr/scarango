package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiClusterEndpoints(client: HttpClient) {
  val get = new ApiClusterEndpointsGet(client)
}