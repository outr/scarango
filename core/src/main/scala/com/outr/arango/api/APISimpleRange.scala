package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APISimpleRange(client: HttpClient) {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **right**: The upper bound.
  *   - **attribute**: The attribute path to check.
  *   - **collection**: The name of the collection to query.
  *   - **limit**: The maximal amount of documents to return. The *skip*
  *    is applied before the *limit* restriction. (optional)
  *   - **closed**: If *true*, use interval including *left* and *right*,
  *    otherwise exclude *right*, but include *left*.
  *   - **skip**: The number of documents to skip in the query (optional).
  *   - **left**: The lower bound.
  * 
  * 
  * 
  * 
  * 
  * This will find all documents within a given range. In order to execute a
  * range query, a skip-list index on the queried attribute must be present.
  * 
  * Returns a cursor containing the result, see [HTTP Cursor](../AqlQueryCursor/README.md) for details.
  * 
  * Note: the *range* simple query is **deprecated** as of ArangoDB 2.6. 
  * The function may be removed in future versions of ArangoDB. The preferred
  * way for retrieving documents from a collection within a specific range
  * is to use an AQL query as follows: 
  * 
  *     FOR doc IN @@collection 
  *       FILTER doc.value >= @left && doc.value < @right 
  *       LIMIT @skip, @limit 
  *       RETURN doc`
  * 
  * 
  * <!-- Hints Start -->
  * 
  * **Warning:**  
  * This route should no longer be used.
  * All endpoints for Simple Queries are deprecated from version 3.4.0 on.
  * They are superseded by AQL queries.
  * 
  * 
  * 
  * <!-- Hints End -->
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/simple/range</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"attribute"</span> : <span class="hljs-string">"i"</span>, 
  * </code><code>  <span class="hljs-string">"left"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"right"</span> : <span class="hljs-number">4</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"105519"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/105519"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Xke--B"</span>, 
  * </code><code>      <span class="hljs-string">"i"</span> : <span class="hljs-number">2</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"105522"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/105522"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Xke--D"</span>, 
  * </code><code>      <span class="hljs-string">"i"</span> : <span class="hljs-number">3</span> 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"hasMore"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(body: PutAPISimpleRange): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/simple/range", append = true) 
    .restful[PutAPISimpleRange, Json](body)
}