package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiIndex#General(client: HttpClient) {
  val post = new ApiIndex#GeneralPost(client)
}