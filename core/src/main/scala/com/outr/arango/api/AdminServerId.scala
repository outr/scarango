package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminServerId(client: HttpClient) {
  val get = new AdminServerIdGet(client)
}