package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiReplicationLoggerFollow(client: HttpClient) {
  val get = new ApiReplicationLoggerFollowGet(client)
}