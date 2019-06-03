package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiJob{JobId}Get(client: HttpClient) {
  /**
  * Returns the processing status of the specified job. The processing status
  * can be
  * determined by peeking into the HTTP response code of the response.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Querying the status of a done job:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'x-arango-async: store'</span> --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/version</span>
  * </code><code>
  * </code><code>HTTP/1.1 Accepted
  * </code><code>content-type: text/plain; charset=utf-8
  * </code><code>x-arango-async-id: 132291
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/job/132291</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-arango-<span class="hljs-keyword">async</span>-id: <span class="hljs-number">132291</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"server"</span> : <span class="hljs-string">"arango"</span>, 
  * </code><code>  <span class="hljs-string">"version"</span> : <span class="hljs-string">"3.5.0-devel"</span>, 
  * </code><code>  <span class="hljs-string">"license"</span> : <span class="hljs-string">"enterprise"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Querying the status of a pending job:
  * (therefore we create a long runnging job...)
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
  * </code><code>x-arango-async-id: 132293
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/job/132293</span>
  * </code><code>
  * </code><code>HTTP/1.1 No Content
  * </code><code>content-type: text/plain; charset=utf-8
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code></pre>
  */
  def get(jobId: String): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .params("job-id" -> job-id.toString)
    .call[ArangoResponse]
}