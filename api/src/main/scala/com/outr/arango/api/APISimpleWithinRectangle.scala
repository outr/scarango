package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APISimpleWithinRectangle {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **latitude1**: The latitude of the first rectangle coordinate.
  *   - **skip**: The number of documents to skip in the query. (optional)
  *   - **latitude2**: The latitude of the second rectangle coordinate.
  *   - **longitude2**: The longitude of the second rectangle coordinate.
  *   - **longitude1**: The longitude of the first rectangle coordinate.
  *   - **limit**: The maximal amount of documents to return. The *skip* is
  *    applied before the *limit* restriction. The default is 100. (optional)
  *   - **collection**: The name of the collection to query.
  *   - **geo**: If given, the identifier of the geo-index to use. (optional)
  * 
  * 
  * 
  * 
  * 
  * This will find all documents within the specified rectangle (determined by
  * the given coordinates (*latitude1*, *longitude1*, *latitude2*, *longitude2*). 
  * 
  * In order to use the *within-rectangle* query, a geo index must be defined for
  * the collection. This index also defines which attribute holds the
  * coordinates for the document.  If you have more than one geo-spatial index,
  * you can use the *geo* field to select a particular index.
  * 
  * Returns a cursor containing the result, see [HTTP Cursor](../AqlQueryCursor/README.md) for details.
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/simple/within-rectangle</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"latitude1"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"longitude1"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"latitude2"</span> : <span class="hljs-number">0.2</span>, 
  * </code><code>  <span class="hljs-string">"longitude2"</span> : <span class="hljs-number">0.2</span>, 
  * </code><code>  <span class="hljs-string">"skip"</span> : <span class="hljs-number">1</span>, 
  * </code><code>  <span class="hljs-string">"limit"</span> : <span class="hljs-number">2</span> 
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
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"105956"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/105956"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1X3O--J"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Name/0.008/"</span>, 
  * </code><code>      <span class="hljs-string">"loc"</span> : [ 
  * </code><code>        <span class="hljs-number">0.008</span>, 
  * </code><code>        <span class="hljs-number">0</span> 
  * </code><code>      ] 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"105953"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/105953"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1X3O--H"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Name/0.006/"</span>, 
  * </code><code>      <span class="hljs-string">"loc"</span> : [ 
  * </code><code>        <span class="hljs-number">0.006</span>, 
  * </code><code>        <span class="hljs-number">0</span> 
  * </code><code>      ] 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"hasMore"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(client: HttpClient, body: PutAPISimpleWithinRectangle)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/simple/within-rectangle", append = true) 
    .restful[PutAPISimpleWithinRectangle, Json](body)
}