package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiCollection{CollectionName}Revision(client: HttpClient) {
  val get = new ApiCollection{CollectionName}RevisionGet(client)
}