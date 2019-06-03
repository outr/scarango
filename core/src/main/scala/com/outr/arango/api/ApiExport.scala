package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiExport(client: HttpClient) {
  val post = new ApiExportPost(client)
}