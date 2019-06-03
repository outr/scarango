package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiFoxxService(client: HttpClient) {
  val delete = new ApiFoxxServiceDelete(client)
  val get = new ApiFoxxServiceGet(client)
  val patch = new ApiFoxxServicePatch(client)
  val put = new ApiFoxxServicePut(client)
}