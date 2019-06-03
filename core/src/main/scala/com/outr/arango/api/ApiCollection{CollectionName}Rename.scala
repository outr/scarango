package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiCollection{CollectionName}Rename(client: HttpClient) {
  val put = new ApiCollection{CollectionName}RenamePut(client)
}