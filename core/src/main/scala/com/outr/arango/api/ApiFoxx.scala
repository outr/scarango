package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiFoxx(client: HttpClient) {
  val post = new ApiFoxxPost(client)
  val commit = new ApiFoxxCommit(client)
  val tests = new ApiFoxxTests(client)
  val service = new ApiFoxxService(client)
  val configuration = new ApiFoxxConfiguration(client)
  val readme = new ApiFoxxReadme(client)
  val swagger = new ApiFoxxSwagger(client)
  val development = new ApiFoxxDevelopment(client)
  val get = new ApiFoxxGet(client)
  val scripts = new ApiFoxxScripts(client)
  val download = new ApiFoxxDownload(client)
  val dependencies = new ApiFoxxDependencies(client)
}