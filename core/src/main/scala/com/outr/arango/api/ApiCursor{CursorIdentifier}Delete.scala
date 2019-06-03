package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiCursor{CursorIdentifier}Delete(client: HttpClient) {
  /**
  * Deletes the cursor and frees the resources associated with it.
  * 
  * The cursor will automatically be destroyed on the server when the client has
  * retrieved all documents from it. The client can also explicitly destroy the
  * cursor at any earlier time using an HTTP DELETE request. The cursor id must
  * be included as part of the URL.
  * 
  * Note: the server will also destroy abandoned cursors automatically after a
  * certain server-controlled timeout to avoid resource leakage.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/cursor</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"query"</span> : <span class="hljs-string">"FOR p IN products LIMIT 5 RETURN p"</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"batchSize"</span> : <span class="hljs-number">2</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103456"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103456"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1M5---H"</span>, 
  * </code><code>      <span class="hljs-string">"hello5"</span> : <span class="hljs-string">"world1"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103443"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103443"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1M5---_"</span>, 
  * </code><code>      <span class="hljs-string">"hello1"</span> : <span class="hljs-string">"world1"</span> 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"hasMore"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"103459"</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-number">5</span>, 
  * </code><code>  <span class="hljs-string">"extra"</span> : { 
  * </code><code>    <span class="hljs-string">"stats"</span> : { 
  * </code><code>      <span class="hljs-string">"writesExecuted"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"writesIgnored"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"scannedFull"</span> : <span class="hljs-number">5</span>, 
  * </code><code>      <span class="hljs-string">"scannedIndex"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"filtered"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"httpRequests"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"executionTime"</span> : <span class="hljs-number">0.00012540817260742188</span>, 
  * </code><code>      <span class="hljs-string">"peakMemoryUsage"</span> : <span class="hljs-number">18120</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"warnings"</span> : [ ] 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"cached"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/cursor/103459</span>
  * </code><code>
  * </code></pre>
  */
  def delete(cursorIdentifier: String): Future[ArangoResponse] = client
    .method(HttpMethod.Delete)
    .params("cursor-identifier" -> cursor-identifier.toString)
    .call[ArangoResponse]
}