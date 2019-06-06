package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
object APISimpleFirstExample {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **example**: The example document.
  *   - **collection**: The name of the collection to query.
  * 
  * 
  * 
  * 
  * 
  * This will return the first document matching a given example.
  * 
  * Returns a result containing the document or *HTTP 404* if no
  * document matched the example.
  * 
  * If more than one document in the collection matches the specified example, only
  * one of these documents will be returned, and it is undefined which of the matching
  * documents is returned.
  * 
  * 
  * <!-- Hints Start -->
  * 
  * **Warning:**  
  * This route should no longer be used.
  * All endpoints for Simple Queries are deprecated from version 3.4.0 on.
  * They are superseded by AQL queries.
  * 
  * 
  * 
  * 
  * **Warning:**  
  * Till ArangoDB versions 3.2.13 and 3.3.7 this API is quite expensive.
  * A more lightweight alternative is to use the [HTTP Cursor API](../AqlQueryCursor/README.md).
  * Starting from versions 3.2.14 and 3.3.8 this performance impact is not
  * an issue anymore, as the internal implementation of the API has changed.
  * 
  * 
  * 
  * <!-- Hints End -->
  * 
  * 
  * **Example:**
  *  If a matching document was found
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/simple/first-example</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"example"</span> : { 
  * </code><code>    <span class="hljs-string">"i"</span> : <span class="hljs-number">1</span> 
  * </code><code>  } 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"document"</span> : { 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"105277"</span>, 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/105277"</span>, 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1XZG--D"</span>, 
  * </code><code>    <span class="hljs-string">"a"</span> : { 
  * </code><code>      <span class="hljs-string">"k"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"j"</span> : <span class="hljs-number">2</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"i"</span> : <span class="hljs-number">1</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  If no document was found
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/simple/first-example</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"example"</span> : { 
  * </code><code>    <span class="hljs-string">"l"</span> : <span class="hljs-number">1</span> 
  * </code><code>  } 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Not Found
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">404</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">404</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"no match"</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(client: HttpClient, body: PutAPISimpleFirstExample): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/simple/first-example", append = true) 
    .restful[PutAPISimpleFirstExample, Json](body)
}