package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIIndex {
  /**
  * Returns an object with an attribute *indexes* containing an array of all
  * index descriptions for the given collection. The same information is also
  * available in the *identifiers* as an object with the index handles as
  * keys.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Return information about all indexes
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/index?collection=products</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"indexes"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"fields"</span> : [ 
  * </code><code>        <span class="hljs-string">"_key"</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"products/0"</span>, 
  * </code><code>      <span class="hljs-string">"selectivityEstimate"</span> : <span class="hljs-number">1</span>, 
  * </code><code>      <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"primary"</span>, 
  * </code><code>      <span class="hljs-string">"unique"</span> : <span class="hljs-literal">true</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"deduplicate"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"fields"</span> : [ 
  * </code><code>        <span class="hljs-string">"name"</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"products/104728"</span>, 
  * </code><code>      <span class="hljs-string">"selectivityEstimate"</span> : <span class="hljs-number">1</span>, 
  * </code><code>      <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"hash"</span>, 
  * </code><code>      <span class="hljs-string">"unique"</span> : <span class="hljs-literal">false</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"deduplicate"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"fields"</span> : [ 
  * </code><code>        <span class="hljs-string">"price"</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"products/104731"</span>, 
  * </code><code>      <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"skiplist"</span>, 
  * </code><code>      <span class="hljs-string">"unique"</span> : <span class="hljs-literal">false</span> 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"identifiers"</span> : { 
  * </code><code>    <span class="hljs-string">"products/0"</span> : { 
  * </code><code>      <span class="hljs-string">"fields"</span> : [ 
  * </code><code>        <span class="hljs-string">"_key"</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"products/0"</span>, 
  * </code><code>      <span class="hljs-string">"selectivityEstimate"</span> : <span class="hljs-number">1</span>, 
  * </code><code>      <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"primary"</span>, 
  * </code><code>      <span class="hljs-string">"unique"</span> : <span class="hljs-literal">true</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"products/104728"</span> : { 
  * </code><code>      <span class="hljs-string">"deduplicate"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"fields"</span> : [ 
  * </code><code>        <span class="hljs-string">"name"</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"products/104728"</span>, 
  * </code><code>      <span class="hljs-string">"selectivityEstimate"</span> : <span class="hljs-number">1</span>, 
  * </code><code>      <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"hash"</span>, 
  * </code><code>      <span class="hljs-string">"unique"</span> : <span class="hljs-literal">false</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"products/104731"</span> : { 
  * </code><code>      <span class="hljs-string">"deduplicate"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"fields"</span> : [ 
  * </code><code>        <span class="hljs-string">"price"</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"products/104731"</span>, 
  * </code><code>      <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"skiplist"</span>, 
  * </code><code>      <span class="hljs-string">"unique"</span> : <span class="hljs-literal">false</span> 
  * </code><code>    } 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def get(client: HttpClient, collection: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/index", append = true) 
    .params("collection" -> collection.toString)
    .call[Json]
}