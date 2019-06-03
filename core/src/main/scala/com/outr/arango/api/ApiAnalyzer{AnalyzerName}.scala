package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiAnalyzer{AnalyzerName}(client: HttpClient) {
  val delete = new ApiAnalyzer{AnalyzerName}Delete(client)
  val get = new ApiAnalyzer{AnalyzerName}Get(client)
}