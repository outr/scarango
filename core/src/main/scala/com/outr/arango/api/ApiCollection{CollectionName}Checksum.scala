package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiCollection{CollectionName}Checksum(client: HttpClient) {
  val get = new ApiCollection{CollectionName}ChecksumGet(client)
}