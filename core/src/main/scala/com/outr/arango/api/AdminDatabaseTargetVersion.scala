package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminDatabaseTargetVersion(client: HttpClient) {
  val get = new AdminDatabaseTargetVersionGet(client)
}