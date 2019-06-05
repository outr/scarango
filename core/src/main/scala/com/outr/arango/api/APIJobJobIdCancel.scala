package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIJobJobIdCancel(client: HttpClient) {
  /**
  * Cancels the currently running job identified by job-id. Note that it still
  * might take some time to actually cancel the running async job.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'x-arango-async: store'</span> --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/cursor</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"query"</span> : <span class="hljs-string">"FOR i IN 1..10 FOR j IN 1..10 LET x = sleep(1.0) FILTER i == 5 &amp;&amp; j == 5 RETURN 42"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/1.1 Accepted
  * </code><code>content-type: text/plain; charset=utf-8
  * </code><code>x-arango-async-id: 132268
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/job/pending</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>[ 
  * </code><code>  <span class="hljs-string">"132268"</span> 
  * </code><code>]
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/job/132268/cancel</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : <span class="hljs-literal">true</span> 
  * </code><code>}
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/job/pending</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>[ 
  * </code><code>  <span class="hljs-string">"132268"</span> 
  * </code><code>]
  * </code></pre>
  */
  def put(jobId: String): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/job/{job-id}/cancel".withArguments(Map("job-id" -> jobId)), append = true)
    .call[Json]
}