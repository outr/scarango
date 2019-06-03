package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiCluster(client: HttpClient) {
  val endpoints = new ApiClusterEndpoints(client)
}