package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminServerAvailability(client: HttpClient) {
  val get = new AdminServerAvailabilityGet(client)
}