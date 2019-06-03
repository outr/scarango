package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiImport#Document(client: HttpClient) {
  val post = new ApiImport#DocumentPost(client)
}