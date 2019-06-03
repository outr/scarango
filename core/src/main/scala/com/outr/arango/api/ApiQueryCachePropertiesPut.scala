package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiQueryCachePropertiesPut(client: HttpClient) {
  /**
  * After the properties have been changed, the current set of properties will
  * be returned in the HTTP response.
  * 
  * Note: changing the properties may invalidate all results in the cache.
  * The global properties for AQL query cache.
  * The properties need to be passed in the attribute *properties* in the body
  * of the HTTP request. *properties* needs to be a JSON object with the following
  * properties:
  * 
  * 
  * **A JSON object with these properties is required:**
  * 
  *   - **maxResultsSize**: the maximum cumulated size of query results that will be stored per database-specific cache.
  *   - **includeSystem**: whether or not to store results of queries that involve system collections.
  *   - **mode**:  the mode the AQL query cache should operate in. Possible values are *off*, *on* or *demand*.
  *   - **maxResults**: the maximum number of query results that will be stored per database-specific cache.
  *   - **maxEntrySize**: the maximum individual size of query results that will be stored per database-specific cache.
  */
  def put(body: PutApiQueryCacheProperties): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .restful[PutApiQueryCacheProperties, ArangoResponse](body)
}