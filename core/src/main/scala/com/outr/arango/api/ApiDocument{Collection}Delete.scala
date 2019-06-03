package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiDocument{Collection}Delete(client: HttpClient) {
  /**
  * The body of the request is an array consisting of selectors for
  * documents. A selector can either be a string with a key or a string
  * with a document handle or an object with a *_key* attribute. This
  * API call removes all specified documents from *collection*. If the
  * selector is an object and has a *_rev* attribute, it is a
  * precondition that the actual revision of the removed document in the
  * collection is the specified one.
  * 
  * The body of the response is an array of the same length as the input
  * array. For each input selector, the output contains a JSON object
  * with the information about the outcome of the operation. If no error
  * occurred, an object is built in which the attribute *_id* contains
  * the known *document-handle* of the removed document, *_key* contains
  * the key which uniquely identifies a document in a given collection,
  * and the attribute *_rev* contains the document revision. In case of
  * an error, an object with the attribute *error* set to *true* and
  * *errorCode* set to the error code is built.
  * 
  * If the *waitForSync* parameter is not specified or set to *false*,
  * then the collection's default *waitForSync* behavior is applied.
  * The *waitForSync* query parameter cannot be used to disable
  * synchronization for collections that have a default *waitForSync*
  * value of *true*.
  * 
  * If the query parameter *returnOld* is *true*, then
  * the complete previous revision of the document
  * is returned under the *old* attribute in the result.
  * 
  * Note that if any precondition is violated or an error occurred with
  * some of the documents, the return code is still 200 or 202, but
  * the additional HTTP header *X-Arango-Error-Codes* is set, which
  * contains a map of the error codes that occurred together with their
  * multiplicities, as in: *1200:17,1205:10* which means that in 17
  * cases the error 1200 "revision conflict" and in 10 cases the error
  * 1205 "illegal document handle" has happened.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Using document handle:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/103740</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1O76--B"</span>
  * </code><code>location: <span class="hljs-regexp">/_db/</span>_system/_api/<span class="hljs-built_in">document</span>/products/<span class="hljs-number">103740</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103740"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103740"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1O76--B"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Unknown document handle:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/103775</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Not Found
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"document not found"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">404</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1202</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Revision conflict:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'If-Match: "_YOn1O66--B"'</span> --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/103721</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Precondition Failed
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1O66--_"</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">412</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1200</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"precondition failed"</span>, 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103721"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103721"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1O66--_"</span> 
  * </code><code>}
  * </code></pre>
  */
  def delete(body: IoCirceJson, collection: String, waitForSync: Option[Boolean] = None, returnOld: Option[Boolean] = None, ignoreRevs: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Delete)
    .params("collection" -> collection.toString)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("ignoreRevs", ignoreRevs, None)
    .restful[IoCirceJson, ArangoResponse](body)
}