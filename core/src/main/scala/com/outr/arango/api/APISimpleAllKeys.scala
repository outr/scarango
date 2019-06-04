package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APISimpleAllKeys(client: HttpClient) {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **type**: The type of the result. The following values are allowed:
  *      - *id*: returns an array of document ids (*_id* attributes)
  *      - *key*: returns an array of document keys (*_key* attributes)
  *      - *path*: returns an array of document URI paths. This is the default.
  *   - **collection**: The collection that should be queried
  * 
  * 
  * 
  * 
  * Returns an array of all keys, ids, or URI paths for all documents in the
  * collection identified by *collection*. The type of the result array is
  * determined by the *type* attribute.
  * 
  * Note that the results have no defined order and thus the order should
  * not be relied on.
  * 
  * Note: the *all-keys* simple query is **deprecated** as of ArangoDB 3.4.0.
  * This API may get removed in future versions of ArangoDB. You can use the
  * `/_api/cursor` endpoint instead with one of the below AQL queries depending
  * on the desired result:
  * 
  * - `FOR doc IN @@collection RETURN doc._id` to mimic *type: id*
  * - `FOR doc IN @@collection RETURN doc._key` to mimic *type: key*
  * - `FOR doc IN @@collection RETURN CONCAT("/_db/", CURRENT_DATABASE(), "/_api/document/", doc._id)`
  *   to mimic *type: path*
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
  *  Return all document paths
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/simple/all-keys</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"products"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : [ 
  * </code><code>    <span class="hljs-string">"/_db/_system/_api/document/products/103997"</span>, 
  * </code><code>    <span class="hljs-string">"/_db/_system/_api/document/products/103994"</span>, 
  * </code><code>    <span class="hljs-string">"/_db/_system/_api/document/products/103990"</span> 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"hasMore"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"cached"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"extra"</span> : { 
  * </code><code>    <span class="hljs-string">"stats"</span> : { 
  * </code><code>      <span class="hljs-string">"writesExecuted"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"writesIgnored"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"scannedFull"</span> : <span class="hljs-number">3</span>, 
  * </code><code>      <span class="hljs-string">"scannedIndex"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"filtered"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"httpRequests"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"executionTime"</span> : <span class="hljs-number">0.00018358230590820312</span>, 
  * </code><code>      <span class="hljs-string">"peakMemoryUsage"</span> : <span class="hljs-number">34208</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"warnings"</span> : [ ] 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Return all document keys
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/simple/all-keys</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"id"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : [ 
  * </code><code>    <span class="hljs-string">"products/103975"</span>, 
  * </code><code>    <span class="hljs-string">"products/103972"</span>, 
  * </code><code>    <span class="hljs-string">"products/103968"</span> 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"hasMore"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"cached"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"extra"</span> : { 
  * </code><code>    <span class="hljs-string">"stats"</span> : { 
  * </code><code>      <span class="hljs-string">"writesExecuted"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"writesIgnored"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"scannedFull"</span> : <span class="hljs-number">3</span>, 
  * </code><code>      <span class="hljs-string">"scannedIndex"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"filtered"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"httpRequests"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"executionTime"</span> : <span class="hljs-number">0.0001857280731201172</span>, 
  * </code><code>      <span class="hljs-string">"peakMemoryUsage"</span> : <span class="hljs-number">34040</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"warnings"</span> : [ ] 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Collection does not exist
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/doesnotexist</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Not Found
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"expecting GET /_api/document/&lt;document-handle&gt;"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">404</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1203</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(collection: Option[String] = None, body: PutReadAllDocuments): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .path(path"/_db/_system/_api/simple/all-keys".withArguments(Map()))
    .param[Option[String]]("collection", collection, None)
    .restful[PutReadAllDocuments, ArangoResponse](body)
}