package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIViewViewName(client: HttpClient) {
  /**
  * Drops the view identified by *view-name*.
  * 
  * If the view was successfully dropped, an object is returned with
  * the following attributes:
  * - *error*: *false*
  * - *id*: The identifier of the dropped view
  * 
  * 
  * 
  * 
  * **Example:**
  *  Using an identifier:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/view/107285</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"result"</span> : <span class="hljs-literal">true</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Using a name:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/view/testView</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"result"</span> : <span class="hljs-literal">true</span> 
  * </code><code>}
  * </code></pre>
  */
  def delete(viewName: String): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/view/{view-name}".withArguments(Map("view-name" -> viewName)), append = true)
    .call[Json]

  /**
  * The result is an object describing the view with the following attributes:
  * - *id*: The identifier of the view
  * - *name*: The name of the view
  * - *type*: The type of the view as string
  * 
  * 
  * 
  * 
  * **Example:**
  *  Using an identifier:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/view/107297</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"arangosearch"</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"testView"</span>, 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/107297"</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"107297"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Using a name:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/view/testView</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"arangosearch"</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"testView"</span>, 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/107303"</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"107303"</span> 
  * </code><code>}
  * </code></pre>
  */
  def get(viewName: String): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/view/{view-name}".withArguments(Map("view-name" -> viewName)), append = true)
    .call[Json]
}