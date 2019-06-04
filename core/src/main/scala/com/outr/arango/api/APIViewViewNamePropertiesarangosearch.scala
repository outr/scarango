package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIViewViewNamePropertiesarangosearch(client: HttpClient) {
  /**
  * Changes the properties of a view.
  * 
  * On success an object with the following attributes is returned:
  * - *id*: The identifier of the view
  * - *name*: The name of the view
  * - *type*: The view type
  * - any additional view implementation specific properties
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PATCH --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/view/products/properties</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/107323"</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"107323"</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"arangosearch"</span>, 
  * </code><code>  <span class="hljs-string">"cleanupIntervalStep"</span> : <span class="hljs-number">10</span>, 
  * </code><code>  <span class="hljs-string">"commitIntervalMsec"</span> : <span class="hljs-number">60000</span>, 
  * </code><code>  <span class="hljs-string">"consolidationIntervalMsec"</span> : <span class="hljs-number">60000</span>, 
  * </code><code>  <span class="hljs-string">"consolidationPolicy"</span> : { 
  * </code><code>    <span class="hljs-string">"type"</span> : <span class="hljs-string">"bytes_accum"</span>, 
  * </code><code>    <span class="hljs-string">"threshold"</span> : <span class="hljs-number">0.10000000149011612</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"writebufferActive"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"writebufferIdle"</span> : <span class="hljs-number">64</span>, 
  * </code><code>  <span class="hljs-string">"writebufferSizeMax"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>  <span class="hljs-string">"links"</span> : { 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def patch(viewName: String): Future[ArangoResponse] = client
    .method(HttpMethod.Patch)
    .path(path"/_db/_system/_api/view/{view-name}/properties#arangosearch".withArguments(Map("view-name" -> viewName)))
    .call[ArangoResponse]

  /**
  * Changes the properties of a view.
  * 
  * On success an object with the following attributes is returned:
  * - *id*: The identifier of the view
  * - *name*: The name of the view
  * - *type*: The view type
  * - any additional view implementation specific properties
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/view/products/properties</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/107337"</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"107337"</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"arangosearch"</span>, 
  * </code><code>  <span class="hljs-string">"cleanupIntervalStep"</span> : <span class="hljs-number">10</span>, 
  * </code><code>  <span class="hljs-string">"commitIntervalMsec"</span> : <span class="hljs-number">60000</span>, 
  * </code><code>  <span class="hljs-string">"consolidationIntervalMsec"</span> : <span class="hljs-number">60000</span>, 
  * </code><code>  <span class="hljs-string">"consolidationPolicy"</span> : { 
  * </code><code>    <span class="hljs-string">"type"</span> : <span class="hljs-string">"bytes_accum"</span>, 
  * </code><code>    <span class="hljs-string">"threshold"</span> : <span class="hljs-number">0.10000000149011612</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"writebufferActive"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"writebufferIdle"</span> : <span class="hljs-number">64</span>, 
  * </code><code>  <span class="hljs-string">"writebufferSizeMax"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>  <span class="hljs-string">"links"</span> : { 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def put(viewName: String): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .path(path"/_db/_system/_api/view/{view-name}/properties#arangosearch".withArguments(Map("view-name" -> viewName)))
    .call[ArangoResponse]
}