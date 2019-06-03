package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiCursor{CursorIdentifier}(client: HttpClient) {
  val delete = new ApiCursor{CursorIdentifier}Delete(client)
  val put = new ApiCursor{CursorIdentifier}Put(client)
}