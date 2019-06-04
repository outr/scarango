package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APIEngine(client: HttpClient) {
  /**
  * Returns the storage engine the server is configured to use.
  * The response is a JSON object with the following attributes:
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * is returned in all cases.
  * 
  * - **name**: will be *mmfiles* or *rocksdb*
  * 
  * 
  * 
  * 
  * **Example:**
  *  Return the active storage engine
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/engine</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"mmfiles"</span>, 
  * </code><code>  <span class="hljs-string">"supports"</span> : { 
  * </code><code>    <span class="hljs-string">"dfdb"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"indexes"</span> : [ 
  * </code><code>      <span class="hljs-string">"primary"</span>, 
  * </code><code>      <span class="hljs-string">"edge"</span>, 
  * </code><code>      <span class="hljs-string">"hash"</span>, 
  * </code><code>      <span class="hljs-string">"skiplist"</span>, 
  * </code><code>      <span class="hljs-string">"ttl"</span>, 
  * </code><code>      <span class="hljs-string">"persistent"</span>, 
  * </code><code>      <span class="hljs-string">"geo"</span>, 
  * </code><code>      <span class="hljs-string">"fulltext"</span> 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"aliases"</span> : { 
  * </code><code>      <span class="hljs-string">"indexes"</span> : { 
  * </code><code>      } 
  * </code><code>    } 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def get(): Future[GetEngineRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_api/engine".withArguments(Map()))
    .call[GetEngineRc200]
}