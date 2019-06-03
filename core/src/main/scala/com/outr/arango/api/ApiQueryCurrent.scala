package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiQueryCurrent(client: HttpClient) {
  val get = new ApiQueryCurrentGet(client)
}