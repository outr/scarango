package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIQueryCacheEntries {
  /**
  * Returns an array containing the AQL query results currently stored in the query results
  * cache of the selected database. Each result is a JSON object with the following attributes:
  * 
  * - *hash*: the query result's hash
  * 
  * - *query*: the query string 
  * 
  * - *bindVars*: the query's bind parameters. this attribute is only shown if tracking for
  *   bind variables was enabled at server start
  * 
  * - *size*: the size of the query result and bind parameters, in bytes
  * 
  * - *results*: number of documents/rows in the query result
  * 
  * - *started*: the date and time when the query was stored in the cache
  * 
  * - *hits*: number of times the result was served from the cache (can be 
  *   *0* for queries that were only stored in the cache but were never accessed
  *   again afterwards)
  * 
  * - *runTime*: the query's run time
  * 
  * - *dataSources*: an array of collections/views the query was using
  */
  def get(client: HttpClient)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/query-cache/entries", append = true) 
    .call[Json]
}