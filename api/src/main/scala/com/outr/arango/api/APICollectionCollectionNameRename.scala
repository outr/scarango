package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APICollectionCollectionNameRename {
  /**
  * Renames a collection. Expects an object with the attribute(s)
  * 
  * - *name*: The new name.
  * 
  * It returns an object with the attributes
  * 
  * - *id*: The identifier of the collection.
  * 
  * - *name*: The new name of the collection.
  * 
  * - *status*: The status of the collection as number.
  * 
  * - *type*: The collection type. Valid types are:
  *   - 2: document collection
  *   - 3: edges collection
  * 
  * - *isSystem*: If *true* then the collection is a system collection.
  * 
  * If renaming the collection succeeds, then the collection is also renamed in 
  * all graph definitions inside the `_graphs` collection in the current database.
  * 
  * **Note**: this method is not available in a cluster.
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/collection/products1/rename</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"newname"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>location: <span class="hljs-regexp">/_api/</span>collection/products1/rename
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"newname"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/103309"</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"103309"</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(client: HttpClient, collectionName: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/collection/{collection-name}/rename".withArguments(Map("collection-name" -> collectionName)), append = true)
    .call[Json]
}