package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiQueryCurrentGet(client: HttpClient) {
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
  def get(): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .call[ArangoResponse]
}