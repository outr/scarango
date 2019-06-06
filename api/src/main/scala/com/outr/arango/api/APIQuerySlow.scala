package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
object APIQuerySlow {
  /**
  * Clears the list of slow AQL queries
  */
  def delete(client: HttpClient): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/query/slow", append = true) 
    .call[Json]

  /**
  * Returns an array containing the last AQL queries that are finished and
  * have exceeded the slow query threshold in the selected database.
  * The maximum amount of queries in the list can be controlled by setting
  * the query tracking property `maxSlowQueries`. The threshold for treating
  * a query as *slow* can be adjusted by setting the query tracking property
  * `slowQueryThreshold`.
  * 
  * Each query is a JSON object with the following attributes:
  * 
  * - *id*: the query's id
  * 
  * - *query*: the query string (potentially truncated)
  * 
  * - *bindVars*: the bind parameter values used by the query
  * 
  * - *started*: the date and time when the query was started
  * 
  * - *runTime*: the query's total run time 
  * 
  * - *state*: the query's current execution state (will always be "finished"
  *   for the list of slow queries)
  * 
  * - *stream*: whether or not the query uses a streaming cursor
  */
  def get(client: HttpClient): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/query/slow", append = true) 
    .call[Json]
}