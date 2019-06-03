package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminShutdown(client: HttpClient) {
  val delete = new AdminShutdownDelete(client)
}