package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APICollectionCollectionNameUnload(client: HttpClient) {
  /**
  * Removes a collection from memory. This call does not delete any documents.
  * You can use the collection afterwards; in which case it will be loaded into
  * memory, again. On success an object with the following attributes is
  * returned:
  * 
  * - *id*: The identifier of the collection.
  * 
  * - *name*: The name of the collection.
  * 
  * - *status*: The status of the collection as number.
  * 
  * - *type*: The collection type. Valid types are:
  *   - 2: document collection
  *   - 3: edges collection
  * 
  * - *isSystem*: If *true* then the collection is a system collection.
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
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/collection/products/unload</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>location: <span class="hljs-regexp">/_api/</span>collection/products/unload
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"status"</span> : <span class="hljs-number">4</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/103332"</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"103332"</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(collectionName: String): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .path(path"/_db/_system/_api/collection/{collection-name}/unload".withArguments(Map("collection-name" -> collectionName)))
    .call[ArangoResponse]
}