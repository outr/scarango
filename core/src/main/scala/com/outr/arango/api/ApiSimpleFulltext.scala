package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiSimpleFulltext(client: HttpClient) {
  val put = new ApiSimpleFulltextPut(client)
}