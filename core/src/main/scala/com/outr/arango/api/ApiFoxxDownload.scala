package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiFoxxDownload(client: HttpClient) {
  val post = new ApiFoxxDownloadPost(client)
}