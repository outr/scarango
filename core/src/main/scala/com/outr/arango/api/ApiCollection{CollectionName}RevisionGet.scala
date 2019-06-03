package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiCollection{CollectionName}RevisionGet(client: HttpClient) {
  /**
  * In addition to the above, the result will also contain the
  * collection's revision id. The revision id is a server-generated
  * string that clients can use to check whether data in a collection
  * has changed since the last revision check.
  * 
  * - *revision*: The collection revision id as a string.
  * 
  * 
  * <!-- Hints Start -->
  * 
  * **Warning:**  
  * Accessing collections by their numeric ID is deprecated from version 3.4.0 on.
  * You should reference them via their names instead.
  * 
  * 
  * 
  * <!-- Hints End -->
  * 
  * 
  * **Example:**
  *  Retrieving the revision of a collection
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/collection/products/revision</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>location: <span class="hljs-regexp">/_api/</span>collection/products/revision
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>  <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>  <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>    <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>    <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/103258"</span>, 
  * </code><code>  <span class="hljs-string">"statusString"</span> : <span class="hljs-string">"loaded"</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"103258"</span>, 
  * </code><code>  <span class="hljs-string">"revision"</span> : <span class="hljs-string">"0"</span>, 
  * </code><code>  <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span> 
  * </code><code>}
  * </code></pre>
  */
  def get(collectionName: String): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .params("collection-name" -> collection-name.toString)
    .call[ArangoResponse]
}