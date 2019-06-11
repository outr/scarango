package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIEdgesCollectionId {
  /**
  * Returns an array of edges starting or ending in the vertex identified by
  * *vertex-handle*.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Any direction
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/edges/edges?vertex=vertices/1</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"edges"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"6"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"edges/6"</span>, 
  * </code><code>      <span class="hljs-string">"_from"</span> : <span class="hljs-string">"vertices/2"</span>, 
  * </code><code>      <span class="hljs-string">"_to"</span> : <span class="hljs-string">"vertices/1"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PWK--B"</span>, 
  * </code><code>      <span class="hljs-string">"$label"</span> : <span class="hljs-string">"v2 -&gt; v1"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"7"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"edges/7"</span>, 
  * </code><code>      <span class="hljs-string">"_from"</span> : <span class="hljs-string">"vertices/4"</span>, 
  * </code><code>      <span class="hljs-string">"_to"</span> : <span class="hljs-string">"vertices/1"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PWK--D"</span>, 
  * </code><code>      <span class="hljs-string">"$label"</span> : <span class="hljs-string">"v4 -&gt; v1"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"5"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"edges/5"</span>, 
  * </code><code>      <span class="hljs-string">"_from"</span> : <span class="hljs-string">"vertices/1"</span>, 
  * </code><code>      <span class="hljs-string">"_to"</span> : <span class="hljs-string">"vertices/3"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PWK--_"</span>, 
  * </code><code>      <span class="hljs-string">"$label"</span> : <span class="hljs-string">"v1 -&gt; v3"</span> 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"stats"</span> : { 
  * </code><code>    <span class="hljs-string">"scannedIndex"</span> : <span class="hljs-number">3</span>, 
  * </code><code>    <span class="hljs-string">"filtered"</span> : <span class="hljs-number">0</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  In edges
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/edges/edges?vertex=vertices/1&amp;direction=<span class="hljs-keyword">in</span></span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"edges"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"6"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"edges/6"</span>, 
  * </code><code>      <span class="hljs-string">"_from"</span> : <span class="hljs-string">"vertices/2"</span>, 
  * </code><code>      <span class="hljs-string">"_to"</span> : <span class="hljs-string">"vertices/1"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PYy--H"</span>, 
  * </code><code>      <span class="hljs-string">"$label"</span> : <span class="hljs-string">"v2 -&gt; v1"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"7"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"edges/7"</span>, 
  * </code><code>      <span class="hljs-string">"_from"</span> : <span class="hljs-string">"vertices/4"</span>, 
  * </code><code>      <span class="hljs-string">"_to"</span> : <span class="hljs-string">"vertices/1"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PYy--J"</span>, 
  * </code><code>      <span class="hljs-string">"$label"</span> : <span class="hljs-string">"v4 -&gt; v1"</span> 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"stats"</span> : { 
  * </code><code>    <span class="hljs-string">"scannedIndex"</span> : <span class="hljs-number">2</span>, 
  * </code><code>    <span class="hljs-string">"filtered"</span> : <span class="hljs-number">0</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Out edges
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/edges/edges?vertex=vertices/1&amp;direction=out</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"edges"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"5"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"edges/5"</span>, 
  * </code><code>      <span class="hljs-string">"_from"</span> : <span class="hljs-string">"vertices/1"</span>, 
  * </code><code>      <span class="hljs-string">"_to"</span> : <span class="hljs-string">"vertices/3"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Pba--F"</span>, 
  * </code><code>      <span class="hljs-string">"$label"</span> : <span class="hljs-string">"v1 -&gt; v3"</span> 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"stats"</span> : { 
  * </code><code>    <span class="hljs-string">"scannedIndex"</span> : <span class="hljs-number">1</span>, 
  * </code><code>    <span class="hljs-string">"filtered"</span> : <span class="hljs-number">0</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def get(client: HttpClient, collectionId: String, vertex: String, direction: Option[String] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/edges/{collection-id}".withArguments(Map("collection-id" -> collectionId)), append = true)
    .params("vertex" -> vertex.toString)
    .param[Option[String]]("direction", direction, None)
    .call[Json]
}