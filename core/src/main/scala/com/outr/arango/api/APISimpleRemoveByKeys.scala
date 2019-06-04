package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APISimpleRemoveByKeys(client: HttpClient) {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **keys** (string): array with the _keys of documents to remove.
  *   - **options**:
  *     - **returnOld**: if set to *true* and *silent* above is *false*, then the above
  *     information about the removed documents contains the complete
  *     removed documents.
  *     - **silent**: if set to *false*, then the result will contain an additional
  *     attribute *old* which contains an array with one entry for each
  *     removed document. By default, these entries will have the *_id*,
  *     *_key* and *_rev* attributes.
  *     - **waitForSync**: if set to true, then all removal operations will
  *     instantly be synchronized to disk. If this is not specified, then the
  *     collection's default sync behavior will be applied.
  *   - **collection**: The name of the collection to look in for the documents to remove
  * 
  * 
  * 
  * 
  * Looks up the documents in the specified collection using the array of keys
  * provided, and removes all documents from the collection whose keys are
  * contained in the *keys* array. Keys for which no document can be found in
  * the underlying collection are ignored, and no exception will be thrown for
  * them.
  * 
  * Equivalent AQL query (the RETURN clause is optional):
  * 
  *     FOR key IN @keys REMOVE key IN @@collection
  *       RETURN OLD
  * 
  * The body of the response contains a JSON object with information how many
  * documents were removed (and how many were not). The *removed* attribute will
  * contain the number of actually removed documents. The *ignored* attribute 
  * will contain the number of keys in the request for which no matching document
  * could be found.
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/simple/remove-by-keys</span> &lt;&lt;EOF
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
  * </code><code>  <span class="hljs-string">"removed"</span> : <span class="hljs-number">10</span>, 
  * </code><code>  <span class="hljs-string">"ignored"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/simple/remove-by-keys</span> &lt;&lt;EOF
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
  * </code><code>  <span class="hljs-string">"removed"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"ignored"</span> : <span class="hljs-number">3</span>, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(body: RestRemoveByKeys): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .path(path"/_db/_system/_api/simple/remove-by-keys".withArguments(Map()))
    .restful[RestRemoveByKeys, ArangoResponse](body)
}