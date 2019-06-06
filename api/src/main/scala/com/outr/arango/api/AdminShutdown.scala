package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
object AdminShutdown {
  /**
  * This call initiates a clean shutdown sequence. Requires administrive privileges
  */
  def delete(client: HttpClient): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_admin/shutdown", append = true) 
    .call[Json]
}