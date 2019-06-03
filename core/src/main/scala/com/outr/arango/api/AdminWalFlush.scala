package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminWalFlush(client: HttpClient) {
  val put = new AdminWalFlushPut(client)
}