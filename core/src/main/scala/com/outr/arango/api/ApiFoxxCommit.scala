package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiFoxxCommit(client: HttpClient) {
  val post = new ApiFoxxCommitPost(client)
}