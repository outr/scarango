package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiDocument{DocumentHandle}Put(client: HttpClient) {
  /**
  * Replaces the document with handle <document-handle> with the one in
  * the body, provided there is such a document and no precondition is
  * violated.
  * 
  * If the *If-Match* header is specified and the revision of the
  * document in the database is unequal to the given revision, the
  * precondition is violated.
  * 
  * If *If-Match* is not given and *ignoreRevs* is *false* and there
  * is a *_rev* attribute in the body and its value does not match
  * the revision of the document in the database, the precondition is
  * violated.
  * 
  * If a precondition is violated, an *HTTP 412* is returned.
  * 
  * If the document exists and can be updated, then an *HTTP 201* or
  * an *HTTP 202* is returned (depending on *waitForSync*, see below),
  * the *Etag* header field contains the new revision of the document
  * and the *Location* header contains a complete URL under which the
  * document can be queried.
  * 
  * Optionally, the query parameter *waitForSync* can be used to force
  * synchronization of the document replacement operation to disk even in case
  * that the *waitForSync* flag had been disabled for the entire collection.
  * Thus, the *waitForSync* query parameter can be used to force synchronization
  * of just specific operations. To use this, set the *waitForSync* parameter
  * to *true*. If the *waitForSync* parameter is not specified or set to
  * *false*, then the collection's default *waitForSync* behavior is
  * applied. The *waitForSync* query parameter cannot be used to disable
  * synchronization for collections that have a default *waitForSync* value
  * of *true*.
  * 
  * If *silent* is not set to *true*, the body of the response contains a JSON 
  * object with the information about the handle and the revision. The attribute 
  * *_id* contains the known *document-handle* of the updated document, *_key* 
  * contains the key which uniquely identifies a document in a given collection, 
  * and the attribute *_rev* contains the new document revision.
  * 
  * If the query parameter *returnOld* is *true*, then
  * the complete previous revision of the document
  * is returned under the *old* attribute in the result.
  * 
  * If the query parameter *returnNew* is *true*, then
  * the complete new document is returned under
  * the *new* attribute in the result.
  * 
  * If the document does not exist, then a *HTTP 404* is returned and the
  * body of the response contains an error document.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Using a document handle
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/document/products/104046</span> &lt;&lt;EOF
  * </code><code>{"Hello": "you"}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Accepted
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PRa--D"</span>
  * </code><code>location: <span class="hljs-regexp">/_db/</span>_system/_api/<span class="hljs-built_in">document</span>/products/<span class="hljs-number">104046</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/104046"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"104046"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PRa--D"</span>, 
  * </code><code>  <span class="hljs-string">"_oldRev"</span> : <span class="hljs-string">"_YOn1PRa--B"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Unknown document handle
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/document/products/104082</span> &lt;&lt;EOF
  * </code><code>{}
  * </code><code>EOF
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
  *  Produce a revision conflict
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'If-Match: "_YOn1PSi--B"'</span> --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/document/products/104063</span> &lt;&lt;EOF
  * </code><code>{"other":"content"}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Precondition Failed
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PSi--_"</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">412</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1200</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"precondition failed"</span>, 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/104063"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"104063"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PSi--_"</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(body: IoCirceJson, documentHandle: String, waitForSync: Option[Boolean] = None, ignoreRevs: Option[Boolean] = None, returnOld: Option[Boolean] = None, returnNew: Option[Boolean] = None, silent: Option[Boolean] = None, IfMatch: Option[String] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .params("document-handle" -> document-handle.toString)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("ignoreRevs", ignoreRevs, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("returnNew", returnNew, None)
    .param[Option[Boolean]]("silent", silent, None)
    .param[Option[String]]("If-Match", If-Match, None)
    .restful[IoCirceJson, ArangoResponse](body)
}