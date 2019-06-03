package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiQueryCacheEntries(client: HttpClient) {
  val get = new ApiQueryCacheEntriesGet(client)
}