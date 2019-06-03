package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminLog(client: HttpClient) {
  val get = new AdminLogGet(client)
  val level = new AdminLogLevel(client)
}