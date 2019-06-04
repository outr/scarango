package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIViewViewNameRename(client: HttpClient) {
  /**
  * Renames a view. Expects an object with the attribute(s)
  * - *name*: The new name
  * 
  * It returns an object with the attributes
  * - *id*: The identifier of the view.
  * - *name*: The new name of the view.
  * - *type*: The view type.
  * 
  * **Note**: this method is not available in a cluster.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/view/products1/rename</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"viewNewName"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"arangosearch"</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"viewNewName"</span>, 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/107345"</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"107345"</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(viewName: String): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .path(path"/_db/_system/_api/view/{view-name}/rename".withArguments(Map("view-name" -> viewName)))
    .call[ArangoResponse]
}