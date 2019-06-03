package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiDocument{DocumentHandle}Get(client: HttpClient) {
  /**
  * Returns the document identified by *document-handle*. The returned
  * document contains three special attributes: *_id* containing the document
  * handle, *_key* containing key which uniquely identifies a document
  * in a given collection and *_rev* containing the revision.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Use a document handle:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/103951</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PLW--B"</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103951"</span>, 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103951"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PLW--B"</span>, 
  * </code><code>  <span class="hljs-string">"hello"</span> : <span class="hljs-string">"world"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Use a document handle and an Etag:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'If-None-Match: "_YOn1PQK--B"'</span> --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/104028</span>
  * </code><code>
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Unknown document handle:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/unknownhandle</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Not Found
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"collection or view not found: products"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">404</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1203</span> 
  * </code><code>}
  * </code></pre>
  */
  def get(documentHandle: String, IfNoneMatch: Option[String] = None, IfMatch: Option[String] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .params("document-handle" -> document-handle.toString)
    .param[Option[String]]("If-None-Match", If-None-Match, None)
    .param[Option[String]]("If-Match", If-Match, None)
    .call[ArangoResponse]
}