package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiGharial{Graph}EdgeGet(client: HttpClient) {
  /**
  * Lists all edge collections within this graph.
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * Is returned if the edge definitions could be listed.
  * 
  * - **code**: The response code.
  * - **collections** (string): The list of all vertex collections within this graph.
  * Includes collections in edgeDefinitions as well as orphans.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is false in this response.
  * 
  * 
  * **HTTP 404**
  * *A json document with these Properties is returned:*
  * 
  * Returned if no graph with this name could be found.
  * 
  * - **errorMessage**: A message created for this error.
  * - **errorNum**: ArangoDB error number for the error that occured.
  * - **code**: The response code.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is true in this response.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/gharial/social/edge</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"collections"</span> : [ 
  * </code><code>    <span class="hljs-string">"relation"</span> 
  * </code><code>  ] 
  * </code><code>}
  * </code></pre>
  */
  def get(graph: String): Future[GeneralGraphListEdgeHttpExamplesRc200] = client
    .method(HttpMethod.Get)
    .params("graph" -> graph.toString)
    .call[GeneralGraphListEdgeHttpExamplesRc200]
}