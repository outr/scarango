package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminClusterHealth(client: HttpClient) {
  val get = new AdminClusterHealthGet(client)
}