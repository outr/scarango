package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminStatus(client: HttpClient) {
  val get = new AdminStatusGet(client)
}