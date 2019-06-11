package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APICollectionCollectionNameLoad {
  /**
  * Loads a collection into memory. Returns the collection on success.
  * 
  * The request body object might optionally contain the following attribute:
  * 
  * - *count*: If set, this controls whether the return value should include
  *   the number of documents in the collection. Setting *count* to
  *   *false* may speed up loading a collection. The default value for
  *   *count* is *true*.
  * 
  * On success an object with the following attributes is returned:
  * 
  * - *id*: The identifier of the collection.
  * 
  * - *name*: The name of the collection.
  * 
  * - *count*: The number of documents inside the collection. This is only
  *   returned if the *count* input parameters is set to *true* or has
  *   not been specified.
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/collection/products/load</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>location: <span class="hljs-regexp">/_api/</span>collection/products/load
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/103270"</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"103270"</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(client: HttpClient, collectionName: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/collection/{collection-name}/load".withArguments(Map("collection-name" -> collectionName)), append = true)
    .call[Json]
}