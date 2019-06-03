package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminServer(client: HttpClient) {
  val availability = new AdminServerAvailability(client)
  val id = new AdminServerId(client)
  val mode = new AdminServerMode(client)
  val role = new AdminServerRole(client)
}