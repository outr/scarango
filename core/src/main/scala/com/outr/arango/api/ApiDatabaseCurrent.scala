package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiDatabaseCurrent(client: HttpClient) {
  val get = new ApiDatabaseCurrentGet(client)
}