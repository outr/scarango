package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIViewViewNameProperties {
  /**
  * Returns an object containing the definition of the view identified by *view-name*.
  * 
  * 
  * 
  * The result is an object describing the view with the following attributes:
  * - *id*: The identifier of the view
  * - *name*: The name of the view
  * - *type*: The type of the view as string
  * - any additional view implementation specific properties
  * 
  * 
  * 
  * 
  * **Example:**
  *  Using an identifier:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/view/107309/properties</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"writebufferIdle"</span> : <span class="hljs-number">64</span>, 
  * </code><code>  <span class="hljs-string">"writebufferActive"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"arangosearch"</span>, 
  * </code><code>  <span class="hljs-string">"writebufferSizeMax"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"commitIntervalMsec"</span> : <span class="hljs-number">60000</span>, 
  * </code><code>  <span class="hljs-string">"consolidationPolicy"</span> : { 
  * </code><code>    <span class="hljs-string">"type"</span> : <span class="hljs-string">"bytes_accum"</span>, 
  * </code><code>    <span class="hljs-string">"threshold"</span> : <span class="hljs-number">0.10000000149011612</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/107309"</span>, 
  * </code><code>  <span class="hljs-string">"cleanupIntervalStep"</span> : <span class="hljs-number">10</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"107309"</span>, 
  * </code><code>  <span class="hljs-string">"links"</span> : { 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"consolidationIntervalMsec"</span> : <span class="hljs-number">60000</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Using a name:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/view/products/properties</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"writebufferIdle"</span> : <span class="hljs-number">64</span>, 
  * </code><code>  <span class="hljs-string">"writebufferActive"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"arangosearch"</span>, 
  * </code><code>  <span class="hljs-string">"writebufferSizeMax"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"commitIntervalMsec"</span> : <span class="hljs-number">60000</span>, 
  * </code><code>  <span class="hljs-string">"consolidationPolicy"</span> : { 
  * </code><code>    <span class="hljs-string">"type"</span> : <span class="hljs-string">"bytes_accum"</span>, 
  * </code><code>    <span class="hljs-string">"threshold"</span> : <span class="hljs-number">0.10000000149011612</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/107316"</span>, 
  * </code><code>  <span class="hljs-string">"cleanupIntervalStep"</span> : <span class="hljs-number">10</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"107316"</span>, 
  * </code><code>  <span class="hljs-string">"links"</span> : { 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"consolidationIntervalMsec"</span> : <span class="hljs-number">60000</span> 
  * </code><code>}
  * </code></pre>
  */
  def get(client: HttpClient, viewName: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/view/{view-name}/properties".withArguments(Map("view-name" -> viewName)), append = true)
    .call[Json]
}