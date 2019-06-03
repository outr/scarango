package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiWal(client: HttpClient) {
  val lastTick = new ApiWalLastTick(client)
  val range = new ApiWalRange(client)
  val tail = new ApiWalTail(client)
}