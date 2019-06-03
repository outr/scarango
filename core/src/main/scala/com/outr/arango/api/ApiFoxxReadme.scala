package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiFoxxReadme(client: HttpClient) {
  val get = new ApiFoxxReadmeGet(client)
}