package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class AdminClusterMaintenance(client: HttpClient) {
  /**
  * This API allows you to temporarily enable the supervision maintenance mode. Be aware that no 
  * automatic failovers of any kind will take place while the maintenance mode is enabled.
  * The _cluster_ supervision reactivates itself automatically _60 minutes_ after disabling it.
  * 
  * To enable the maintenance mode the request body must contain the string `"on"`. To disable it, send the string
  * `"off"` (Please note it _must_ be lowercase as well as include the quotes).
  */
  def put(): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_admin/cluster/maintenance", append = true) 
    .call[Json]
}