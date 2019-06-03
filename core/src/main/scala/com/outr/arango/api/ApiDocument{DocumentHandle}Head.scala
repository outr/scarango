package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiDocument{DocumentHandle}Head(client: HttpClient) {
  /**
  * Like *GET*, but only returns the header fields and not the body. You
  * can use this call to get the current revision of a document or check if
  * the document was deleted.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X HEAD --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/104012</span>
  * </code><code>
  * </code></pre>
  */
  def head(documentHandle: String, IfNoneMatch: Option[String] = None, IfMatch: Option[String] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Head)
    .params("document-handle" -> document-handle.toString)
    .param[Option[String]]("If-None-Match", If-None-Match, None)
    .param[Option[String]]("If-Match", If-Match, None)
    .call[ArangoResponse]
}