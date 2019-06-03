package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminLogLevel(client: HttpClient) {
  val get = new AdminLogLevelGet(client)
  val put = new AdminLogLevelPut(client)
}