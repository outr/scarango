package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminTime(client: HttpClient) {
  val get = new AdminTimeGet(client)
}