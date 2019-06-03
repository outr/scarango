package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiJob{Type}Get(client: HttpClient) {
  /**
  * Returns the list of ids of async jobs with a specific status (either done or
  * pending).
  * The list can be used by the client to get an overview of the job system
  * status and
  * to retrieve completed job results later.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Fetching the list of done jobs:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'x-arango-async: store'</span> --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/version</span>
  * </code><code>
  * </code><code>HTTP/1.1 Accepted
  * </code><code>content-type: text/plain; charset=utf-8
  * </code><code>x-arango-async-id: 132284
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/job/<span class="hljs-keyword">done</span></span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>[ 
  * </code><code>  <span class="hljs-string">"132284"</span> 
  * </code><code>]
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Fetching the list of pending jobs:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'x-arango-async: store'</span> --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/version</span>
  * </code><code>
  * </code><code>HTTP/1.1 Accepted
  * </code><code>content-type: text/plain; charset=utf-8
  * </code><code>x-arango-async-id: 132286
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/job/pending</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>[ ]
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Querying the status of a pending job:
  * (we create a sleep job therefore...)
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'x-arango-async: store'</span> --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/transaction</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collections"</span> : { 
  * </code><code>    <span class="hljs-string">"read"</span> : [ 
  * </code><code>      <span class="hljs-string">"_frontend"</span> 
  * </code><code>    ] 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"action"</span> : <span class="hljs-string">"function () {require('internal').sleep(15.0);}"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/1.1 Accepted
  * </code><code>content-type: text/plain; charset=utf-8
  * </code><code>x-arango-async-id: 132288
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/job/pending</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>[ 
  * </code><code>  <span class="hljs-string">"132288"</span> 
  * </code><code>]
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/job/132288</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : <span class="hljs-literal">true</span> 
  * </code><code>}
  * </code></pre>
  */
  def get(`type`: String, count: Option[Double] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .params("type" -> type.toString)
    .param[Option[Double]]("count", count, None)
    .call[ArangoResponse]
}