package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiJob{JobId}Put(client: HttpClient) {
  /**
  * Returns the result of an async job identified by job-id. If the async job
  * result is present on the server, the result will be removed from the list of
  * result. That means this method can be called for each job-id once.
  * The method will return the original job result's headers and body, plus the
  * additional HTTP header x-arango-async-job-id. If this header is present,
  * then
  * the job was found and the response contains the original job's result. If
  * the header is not present, the job was not found and the response contains
  * status information from the job manager.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Not providing a job-id:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/job</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Bad Request
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"bad parameter"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">400</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">400</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Providing a job-id for a non-existing job:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/job/notthere</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Not Found
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"not found"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">404</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">404</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Fetching the result of an HTTP GET job:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'x-arango-async: store'</span> --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/version</span>
  * </code><code>
  * </code><code>HTTP/1.1 Accepted
  * </code><code>content-type: text/plain; charset=utf-8
  * </code><code>x-arango-async-id: 132280
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/job/132280</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-arango-<span class="hljs-keyword">async</span>-id: <span class="hljs-number">132280</span>
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
  *  Fetching the result of an HTTP POST job that failed:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'x-arango-async: store'</span> --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/collection</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">" this name is invalid "</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/1.1 Accepted
  * </code><code>content-type: text/plain; charset=utf-8
  * </code><code>x-arango-async-id: 132282
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/job/132282</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Bad Request
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-arango-<span class="hljs-keyword">async</span>-id: <span class="hljs-number">132282</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"expected PUT /_api/collection/&lt;collection-name&gt;/&lt;action&gt;"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">400</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">400</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(jobId: String): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .params("job-id" -> job-id.toString)
    .call[ArangoResponse]
}