package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiDatabaseUser(client: HttpClient) {
  val get = new ApiDatabaseUserGet(client)
}