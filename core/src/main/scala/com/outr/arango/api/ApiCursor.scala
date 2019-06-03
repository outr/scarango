package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiCursor(client: HttpClient) {
  val post = new ApiCursorPost(client)
  val {CursorIdentifier} = new ApiCursor{CursorIdentifier}(client)
}