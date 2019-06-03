package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminRouting(client: HttpClient) {
  val reload = new AdminRoutingReload(client)
}