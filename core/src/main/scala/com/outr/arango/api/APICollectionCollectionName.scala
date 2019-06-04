package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APICollectionCollectionName(client: HttpClient) {
  /**
  * Drops the collection identified by *collection-name*.
  * 
  * If the collection was successfully dropped, an object is returned with
  * the following attributes:
  * 
  * - *error*: *false*
  * 
  * - *id*: The identifier of the dropped collection.
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
  *  Using an identifier:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/collection/102833</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"102833"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Using a name:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/collection/products1</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"102844"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Dropping a system collection
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/collection/_example?isSystem=<span class="hljs-literal">true</span></span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"102855"</span> 
  * </code><code>}
  * </code></pre>
  */
  def delete(collectionName: String, isSystem: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Delete)
    .path(path"/_db/_system/_api/collection/{collection-name}".withArguments(Map("collection-name" -> collectionName)))
    .param[Option[Boolean]]("isSystem", isSystem, None)
    .call[ArangoResponse]

  /**
  * The result is an object describing the collection with the following
  * attributes:
  * 
  * - *id*: The identifier of the collection.
  * 
  * - *name*: The name of the collection.
  * 
  * - *status*: The status of the collection as number.
  *  - 1: new born collection
  *  - 2: unloaded
  *  - 3: loaded
  *  - 4: in the process of being unloaded
  *  - 5: deleted
  *  - 6: loading
  * 
  * Every other status indicates a corrupted collection.
  * 
  * - *type*: The type of the collection as number.
  *   - 2: document collection (normal case)
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
  */
  def get(collectionName: String): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_api/collection/{collection-name}".withArguments(Map("collection-name" -> collectionName)))
    .call[ArangoResponse]
}