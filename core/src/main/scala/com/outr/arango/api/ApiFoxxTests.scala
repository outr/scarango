package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiFoxxTests(client: HttpClient) {
  val post = new ApiFoxxTestsPost(client)
}