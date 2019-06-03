package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiDatabase{DatabaseName}(client: HttpClient) {
  val delete = new ApiDatabase{DatabaseName}Delete(client)
}