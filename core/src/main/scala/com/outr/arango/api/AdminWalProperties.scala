package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminWalProperties(client: HttpClient) {
  val get = new AdminWalPropertiesGet(client)
  val put = new AdminWalPropertiesPut(client)
}