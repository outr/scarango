package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiVersion(client: HttpClient) {
  val get = new ApiVersionGet(client)
}