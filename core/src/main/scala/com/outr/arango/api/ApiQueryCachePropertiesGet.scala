package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiQueryCachePropertiesGet(client: HttpClient) {
  /**
  * Returns the global AQL query results cache configuration. The configuration is a
  * JSON object with the following properties:
  * 
  * - *mode*: the mode the AQL query results cache operates in. The mode is one of the following
  *   values: *off*, *on* or *demand*.
  * 
  * - *maxResults*: the maximum number of query results that will be stored per database-specific
  *   cache.
  * 
  * - *maxResultsSize*: the maximum cumulated size of query results that will be stored per 
  *   database-specific cache.
  * 
  * - *maxEntrySize*: the maximum individual result size of queries that will be stored per 
  *   database-specific cache.
  * 
  * - *includeSystem*: whether or not results of queries that involve system collections will be
  *   stored in the query results cache.
  */
  def get(): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .call[ArangoResponse]
}