package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
object APISimpleReplaceByExample {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **options**:
  *     - **limit**: an optional value that determines how many documents to
  *     replace at most. If *limit* is specified but is less than the number
  *     of documents in the collection, it is undefined which of the documents
  *     will be replaced.
  *     - **waitForSync**: if set to true, then all removal operations will
  *      instantly be synchronized to disk. If this is not specified, then the
  *      collection's default sync behavior will be applied.
  *   - **example**: An example document that all collection documents are compared against.
  *   - **collection**: The name of the collection to replace within.
  *   - **newValue**: The replacement document that will get inserted in place
  *    of the "old" documents.
  * 
  * 
  * 
  * 
  * 
  * This will find all documents in the collection that match the specified
  * example object, and replace the entire document body with the new value
  * specified. Note that document meta-attributes such as *_id*, *_key*,
  * *_from*, *_to* etc. cannot be replaced.
  * 
  * Note: the *limit* attribute is not supported on sharded collections.
  * Using it will result in an error.
  * 
  * Returns the number of documents that were replaced.
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
  * <!-- Hints End -->
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/simple/replace-by-example</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"example"</span> : { 
  * </code><code>    <span class="hljs-string">"a"</span> : { 
  * </code><code>      <span class="hljs-string">"j"</span> : <span class="hljs-number">1</span> 
  * </code><code>    } 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"newValue"</span> : { 
  * </code><code>    <span class="hljs-string">"foo"</span> : <span class="hljs-string">"bar"</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"limit"</span> : <span class="hljs-number">3</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"replaced"</span> : <span class="hljs-number">1</span>, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Using new Signature for attributes WaitForSync and limit
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/simple/replace-by-example</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"example"</span> : { 
  * </code><code>    <span class="hljs-string">"a"</span> : { 
  * </code><code>      <span class="hljs-string">"j"</span> : <span class="hljs-number">1</span> 
  * </code><code>    } 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"newValue"</span> : { 
  * </code><code>    <span class="hljs-string">"foo"</span> : <span class="hljs-string">"bar"</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"options"</span> : { 
  * </code><code>    <span class="hljs-string">"limit"</span> : <span class="hljs-number">3</span>, 
  * </code><code>    <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">true</span> 
  * </code><code>  } 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"replaced"</span> : <span class="hljs-number">1</span>, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(client: HttpClient, body: PutAPISimpleReplaceByExample): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/simple/replace-by-example", append = true) 
    .restful[PutAPISimpleReplaceByExample, Json](body)
}