package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APISimpleLookupByKeys(client: HttpClient) {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **keys** (string): array with the _keys of documents to remove.
  *   - **collection**: The name of the collection to look in for the documents
  * 
  * 
  * 
  * 
  * Looks up the documents in the specified collection
  * using the array of keys provided. All documents for which a matching
  * key was specified in the *keys* array and that exist in the collection
  * will be returned.  Keys for which no document can be found in the
  * underlying collection are ignored, and no exception will be thrown for
  * them.
  * 
  * Equivalent AQL query:
  * 
  *     FOR doc IN @@collection FILTER doc._key IN @keys RETURN doc
  * 
  * The body of the response contains a JSON object with a *documents*
  * attribute. The *documents* attribute is an array containing the
  * matching documents. The order in which matching documents are present
  * in the result array is unspecified.
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
  *  Looking up existing documents
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/simple/lookup-by-keys</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"keys"</span> : [ 
  * </code><code>    <span class="hljs-string">"test0"</span>, 
  * </code><code>    <span class="hljs-string">"test1"</span>, 
  * </code><code>    <span class="hljs-string">"test2"</span>, 
  * </code><code>    <span class="hljs-string">"test3"</span>, 
  * </code><code>    <span class="hljs-string">"test4"</span>, 
  * </code><code>    <span class="hljs-string">"test5"</span>, 
  * </code><code>    <span class="hljs-string">"test6"</span>, 
  * </code><code>    <span class="hljs-string">"test7"</span>, 
  * </code><code>    <span class="hljs-string">"test8"</span>, 
  * </code><code>    <span class="hljs-string">"test9"</span> 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"test"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"documents"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"test0"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"test/test0"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1XdO--B"</span>, 
  * </code><code>      <span class="hljs-string">"value"</span> : <span class="hljs-number">0</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"test1"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"test/test1"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1XdS--_"</span>, 
  * </code><code>      <span class="hljs-string">"value"</span> : <span class="hljs-number">1</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"test2"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"test/test2"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1XdS--B"</span>, 
  * </code><code>      <span class="hljs-string">"value"</span> : <span class="hljs-number">2</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"test3"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"test/test3"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1XdS--D"</span>, 
  * </code><code>      <span class="hljs-string">"value"</span> : <span class="hljs-number">3</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"test4"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"test/test4"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1XdS--F"</span>, 
  * </code><code>      <span class="hljs-string">"value"</span> : <span class="hljs-number">4</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"test5"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"test/test5"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1XdW--_"</span>, 
  * </code><code>      <span class="hljs-string">"value"</span> : <span class="hljs-number">5</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"test6"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"test/test6"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1XdW--B"</span>, 
  * </code><code>      <span class="hljs-string">"value"</span> : <span class="hljs-number">6</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"test7"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"test/test7"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1XdW--D"</span>, 
  * </code><code>      <span class="hljs-string">"value"</span> : <span class="hljs-number">7</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"test8"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"test/test8"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1XdW--F"</span>, 
  * </code><code>      <span class="hljs-string">"value"</span> : <span class="hljs-number">8</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"test9"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"test/test9"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1XdW--H"</span>, 
  * </code><code>      <span class="hljs-string">"value"</span> : <span class="hljs-number">9</span> 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Looking up non-existing documents
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/simple/lookup-by-keys</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"keys"</span> : [ 
  * </code><code>    <span class="hljs-string">"foo"</span>, 
  * </code><code>    <span class="hljs-string">"bar"</span>, 
  * </code><code>    <span class="hljs-string">"baz"</span> 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"test"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"documents"</span> : [ ], 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(body: RestLookupByKeys): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .path(path"/_db/_system/_api/simple/lookup-by-keys".withArguments(Map()))
    .restful[RestLookupByKeys, ArangoResponse](body)
}