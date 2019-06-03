package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiAnalyzer(client: HttpClient) {
  val get = new ApiAnalyzerGet(client)
  val post = new ApiAnalyzerPost(client)
  val {AnalyzerName} = new ApiAnalyzer{AnalyzerName}(client)
}