package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APISimpleWithin(client: HttpClient) {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **distance**: If given, the attribute key used to return the distance to
  *    the given coordinate. (optional). If specified, distances are returned in meters.
  *   - **skip**: The number of documents to skip in the query. (optional)
  *   - **longitude**: The longitude of the coordinate.
  *   - **radius**: The maximal radius (in meters).
  *   - **collection**: The name of the collection to query.
  *   - **latitude**: The latitude of the coordinate.
  *   - **limit**: The maximal amount of documents to return. The *skip* is
  *    applied before the *limit* restriction. The default is 100. (optional)
  *   - **geo**: If given, the identifier of the geo-index to use. (optional)
  * 
  * 
  * 
  * 
  * 
  * This will find all documents within a given radius around the coordinate
  * (*latitude*, *longitude*). The returned list is sorted by distance.
  * 
  * In order to use the *within* operator, a geo index must be defined for
  * the collection. This index also defines which attribute holds the
  * coordinates for the document.  If you have more than one geo-spatial index,
  * you can use the *geo* field to select a particular index.
  * 
  * 
  * Returns a cursor containing the result, see [HTTP Cursor](../AqlQueryCursor/README.md) for details.
  * 
  * Note: the *within* simple query is **deprecated** as of ArangoDB 2.6. 
  * This API may be removed in future versions of ArangoDB. The preferred
  * way for retrieving documents from a collection using the near operator is
  * to issue an [AQL query](../../AQL/Functions/Geo.html) using the *WITHIN* function as follows: 
  * 
  *     FOR doc IN WITHIN(@@collection, @latitude, @longitude, @radius, @distanceAttributeName)
  *       RETURN doc
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
  *  Without distance
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/simple/near</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"latitude"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"longitude"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"skip"</span> : <span class="hljs-number">1</span>, 
  * </code><code>  <span class="hljs-string">"limit"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"radius"</span> : <span class="hljs-number">500</span> 
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
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"105839"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/105839"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Xx6--D"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Name/-0.002/"</span>, 
  * </code><code>      <span class="hljs-string">"loc"</span> : [ 
  * </code><code>        <span class="hljs-number">-0.002</span>, 
  * </code><code>        <span class="hljs-number">0</span> 
  * </code><code>      ] 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"105845"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/105845"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Xy---_"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Name/0.002/"</span>, 
  * </code><code>      <span class="hljs-string">"loc"</span> : [ 
  * </code><code>        <span class="hljs-number">0.002</span>, 
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
  * 
  * 
  * 
  * 
  * **Example:**
  *  With distance
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/simple/near</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"latitude"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"longitude"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"skip"</span> : <span class="hljs-number">1</span>, 
  * </code><code>  <span class="hljs-string">"limit"</span> : <span class="hljs-number">3</span>, 
  * </code><code>  <span class="hljs-string">"distance"</span> : <span class="hljs-string">"distance"</span>, 
  * </code><code>  <span class="hljs-string">"radius"</span> : <span class="hljs-number">300</span> 
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
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/105890"</span>, 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"105890"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1X1G--H"</span>, 
  * </code><code>      <span class="hljs-string">"loc"</span> : [ 
  * </code><code>        <span class="hljs-number">-0.002</span>, 
  * </code><code>        <span class="hljs-number">0</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Name/-0.002/"</span>, 
  * </code><code>      <span class="hljs-string">"distance"</span> : <span class="hljs-number">222.3898532891175</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/105896"</span>, 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"105896"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1X1K--B"</span>, 
  * </code><code>      <span class="hljs-string">"loc"</span> : [ 
  * </code><code>        <span class="hljs-number">0.002</span>, 
  * </code><code>        <span class="hljs-number">0</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Name/0.002/"</span>, 
  * </code><code>      <span class="hljs-string">"distance"</span> : <span class="hljs-number">222.3898532891175</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/105899"</span>, 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"105899"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1X1K--D"</span>, 
  * </code><code>      <span class="hljs-string">"loc"</span> : [ 
  * </code><code>        <span class="hljs-number">0.004</span>, 
  * </code><code>        <span class="hljs-number">0</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Name/0.004/"</span>, 
  * </code><code>      <span class="hljs-string">"distance"</span> : <span class="hljs-number">444.779706578235</span> 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"hasMore"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-number">3</span>, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(body: PutAPISimpleWithin): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .path(path"/_db/_system/_api/simple/within".withArguments(Map()))
    .restful[PutAPISimpleWithin, ArangoResponse](body)
}