package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
object APISimpleUpdateByExample {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **options**:
  *     - **keepNull**: This parameter can be used to modify the behavior when
  *     handling *null* values. Normally, *null* values are stored in the
  *     database. By setting the *keepNull* parameter to *false*, this
  *     behavior can be changed so that all attributes in *data* with *null*
  *     values will be removed from the updated document.
  *     - **mergeObjects**: Controls whether objects (not arrays) will be merged if present in both the
  *     existing and the patch document. If set to false, the value in the
  *     patch document will overwrite the existing document's value. If set to
  *     true, objects will be merged. The default is true.
  *     - **limit**: an optional value that determines how many documents to
  *     update at most. If *limit* is specified but is less than the number
  *     of documents in the collection, it is undefined which of the documents
  *     will be updated.
  *     - **waitForSync**: if set to true, then all removal operations will
  *     instantly be synchronized to disk. If this is not specified, then the
  *     collection's default sync behavior will be applied.
  *   - **example**: An example document that all collection documents are compared against.
  *   - **collection**: The name of the collection to update within.
  *   - **newValue**: A document containing all the attributes to update in the found documents.
  * 
  * 
  * 
  * 
  * 
  * This will find all documents in the collection that match the specified
  * example object, and partially update the document body with the new value
  * specified. Note that document meta-attributes such as *_id*, *_key*,
  * *_from*, *_to* etc. cannot be replaced.
  * 
  * Note: the *limit* attribute is not supported on sharded collections.
  * Using it will result in an error.
  * 
  * Returns the number of documents that were updated.
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
  *  using old syntax for options
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/simple/update-by-example</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"example"</span> : { 
  * </code><code>    <span class="hljs-string">"a"</span> : { 
  * </code><code>      <span class="hljs-string">"j"</span> : <span class="hljs-number">1</span> 
  * </code><code>    } 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"newValue"</span> : { 
  * </code><code>    <span class="hljs-string">"a"</span> : { 
  * </code><code>      <span class="hljs-string">"j"</span> : <span class="hljs-number">22</span> 
  * </code><code>    } 
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
  * </code><code>  <span class="hljs-string">"updated"</span> : <span class="hljs-number">1</span>, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  using new signature for options
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/simple/update-by-example</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"example"</span> : { 
  * </code><code>    <span class="hljs-string">"a"</span> : { 
  * </code><code>      <span class="hljs-string">"j"</span> : <span class="hljs-number">1</span> 
  * </code><code>    } 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"newValue"</span> : { 
  * </code><code>    <span class="hljs-string">"a"</span> : { 
  * </code><code>      <span class="hljs-string">"j"</span> : <span class="hljs-number">22</span> 
  * </code><code>    } 
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
  * </code><code>  <span class="hljs-string">"updated"</span> : <span class="hljs-number">1</span>, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(client: HttpClient, body: PutAPISimpleUpdateByExample): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/simple/update-by-example", append = true) 
    .restful[PutAPISimpleUpdateByExample, Json](body)
}