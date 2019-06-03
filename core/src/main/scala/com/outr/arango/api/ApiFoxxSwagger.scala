package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiFoxxSwagger(client: HttpClient) {
  val get = new ApiFoxxSwaggerGet(client)
}