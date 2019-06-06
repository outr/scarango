package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
object AdminServerAvailability {
  /**
  * Return availability information about a server.
  * 
  * This is a public API so it does *not* require authentication. It is meant to be
  * used only in the context of server monitoring only.
  */
  def get(client: HttpClient): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_admin/server/availability", append = true) 
    .call[Json]
}