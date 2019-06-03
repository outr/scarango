package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiQuerySlow(client: HttpClient) {
  val delete = new ApiQuerySlowDelete(client)
  val get = new ApiQuerySlowGet(client)
}