package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminServerMode(client: HttpClient) {
  val get = new AdminServerModeGet(client)
  val put = new AdminServerModePut(client)
}