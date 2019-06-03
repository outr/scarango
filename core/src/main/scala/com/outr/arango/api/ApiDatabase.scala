package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiDatabase(client: HttpClient) {
  val post = new ApiDatabasePost(client)
  val get = new ApiDatabaseGet(client)
  val current = new ApiDatabaseCurrent(client)
  val user = new ApiDatabaseUser(client)
  val {DatabaseName} = new ApiDatabase{DatabaseName}(client)
}