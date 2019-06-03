package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class AdminLogLevelGet(client: HttpClient) {
  /**
  * Returns the server's current log level settings.
  * The result is a JSON object with the log topics being the object keys, and
  * the log levels being the object values.
  */
  def get(): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .call[ArangoResponse]
}