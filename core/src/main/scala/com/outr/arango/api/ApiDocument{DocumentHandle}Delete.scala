package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiDocument{DocumentHandle}Delete(client: HttpClient) {
  /**
  * If *silent* is not set to *true*, the body of the response contains a JSON 
  * object with the information about the handle and the revision. The attribute 
  * *_id* contains the known *document-handle* of the removed document, *_key* 
  * contains the key which uniquely identifies a document in a given collection, 
  * and the attribute *_rev* contains the document revision.
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
  * 
  * 
  * 
  * **Example:**
  *  Using document handle:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/103685</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1O4q--_"</span>
  * </code><code>location: <span class="hljs-regexp">/_db/</span>_system/_api/<span class="hljs-built_in">document</span>/products/<span class="hljs-number">103685</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103685"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103685"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1O4q--_"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Unknown document handle:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/103757</span>
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'If-Match: "_YOn1O52--D"'</span> --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/103702</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Precondition Failed
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1O52--B"</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">412</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1200</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"precondition failed"</span>, 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103702"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103702"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1O52--B"</span> 
  * </code><code>}
  * </code></pre>
  */
  def delete(documentHandle: String, waitForSync: Option[Boolean] = None, returnOld: Option[Boolean] = None, silent: Option[Boolean] = None, IfMatch: Option[String] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Delete)
    .params("document-handle" -> document-handle.toString)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("silent", silent, None)
    .param[Option[String]]("If-Match", If-Match, None)
    .call[ArangoResponse]
}