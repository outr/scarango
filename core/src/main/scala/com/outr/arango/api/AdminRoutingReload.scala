package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminRoutingReload(client: HttpClient) {
  val post = new AdminRoutingReloadPost(client)
}