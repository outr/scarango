package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiFoxxDependencies(client: HttpClient) {
  val get = new ApiFoxxDependenciesGet(client)
  val patch = new ApiFoxxDependenciesPatch(client)
  val put = new ApiFoxxDependenciesPut(client)
}