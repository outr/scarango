package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiFoxxDevelopment(client: HttpClient) {
  val delete = new ApiFoxxDevelopmentDelete(client)
  val post = new ApiFoxxDevelopmentPost(client)
}