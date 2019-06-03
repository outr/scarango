package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiFoxxConfiguration(client: HttpClient) {
  val get = new ApiFoxxConfigurationGet(client)
  val patch = new ApiFoxxConfigurationPatch(client)
  val put = new ApiFoxxConfigurationPut(client)
}