package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiDocument{DocumentHandle}(client: HttpClient) {
  val head = new ApiDocument{DocumentHandle}Head(client)
  val delete = new ApiDocument{DocumentHandle}Delete(client)
  val put = new ApiDocument{DocumentHandle}Put(client)
  val get = new ApiDocument{DocumentHandle}Get(client)
  val patch = new ApiDocument{DocumentHandle}Patch(client)
}