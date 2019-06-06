package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
object APIQueryCurrent {
  /**
  * Returns an array containing the AQL queries currently running in the selected
  * database. Each query is a JSON object with the following attributes:
  * 
  * - *id*: the query's id
  * 
  * - *query*: the query string (potentially truncated)
  * 
  * - *bindVars*: the bind parameter values used by the query
  * 
  * - *started*: the date and time when the query was started
  * 
  * - *runTime*: the query's run time up to the point the list of queries was
  *   queried
  * 
  * - *state*: the query's current execution state (as a string)
  * 
  * - *stream*: whether or not the query uses a streaming cursor
  */
  def get(client: HttpClient): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/query/current", append = true) 
    .call[Json]
}