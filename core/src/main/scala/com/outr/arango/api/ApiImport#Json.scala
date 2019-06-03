package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiImport#Json(client: HttpClient) {
  val post = new ApiImport#JsonPost(client)
}