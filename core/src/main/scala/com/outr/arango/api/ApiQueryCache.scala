package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiQueryCache(client: HttpClient) {
  val delete = new ApiQueryCacheDelete(client)
  val entries = new ApiQueryCacheEntries(client)
  val properties = new ApiQueryCacheProperties(client)
}