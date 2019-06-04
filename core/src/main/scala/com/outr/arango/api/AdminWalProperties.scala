package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class AdminWalProperties(client: HttpClient) {
  /**
  * Retrieves the configuration of the write-ahead log. The result is a JSON
  * object with the following attributes:
  * - *allowOversizeEntries*: whether or not operations that are bigger than a
  *   single logfile can be executed and stored
  * - *logfileSize*: the size of each write-ahead logfile
  * - *historicLogfiles*: the maximum number of historic logfiles to keep
  * - *reserveLogfiles*: the maximum number of reserve logfiles that ArangoDB
  *   allocates in the background
  * - *syncInterval*: the interval for automatic synchronization of not-yet
  *   synchronized write-ahead log data (in milliseconds)
  * - *throttleWait*: the maximum wait time that operations will wait before
  *   they get aborted if case of write-throttling (in milliseconds)
  * - *throttleWhenPending*: the number of unprocessed garbage-collection
  *   operations that, when reached, will activate write-throttling. A value of
  *   *0* means that write-throttling will not be triggered.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_admin/wal/properties</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"allowOversizeEntries"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"logfileSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>  <span class="hljs-string">"historicLogfiles"</span> : <span class="hljs-number">10</span>, 
  * </code><code>  <span class="hljs-string">"reserveLogfiles"</span> : <span class="hljs-number">3</span>, 
  * </code><code>  <span class="hljs-string">"syncInterval"</span> : <span class="hljs-number">100</span>, 
  * </code><code>  <span class="hljs-string">"throttleWait"</span> : <span class="hljs-number">15000</span>, 
  * </code><code>  <span class="hljs-string">"throttleWhenPending"</span> : <span class="hljs-number">0</span> 
  * </code><code>}
  * </code></pre>
  */
  def get(): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_admin/wal/properties".withArguments(Map()))
    .call[ArangoResponse]

  /**
  * Configures the behavior of the write-ahead log. The body of the request
  * must be a JSON object with the following attributes:
  * - *allowOversizeEntries*: whether or not operations that are bigger than a
  *   single logfile can be executed and stored
  * - *logfileSize*: the size of each write-ahead logfile
  * - *historicLogfiles*: the maximum number of historic logfiles to keep
  * - *reserveLogfiles*: the maximum number of reserve logfiles that ArangoDB
  *   allocates in the background
  * - *throttleWait*: the maximum wait time that operations will wait before
  *   they get aborted if case of write-throttling (in milliseconds)
  * - *throttleWhenPending*: the number of unprocessed garbage-collection
  *   operations that, when reached, will activate write-throttling. A value of
  *   *0* means that write-throttling will not be triggered.
  * 
  * Specifying any of the above attributes is optional. Not specified attributes
  * will be ignored and the configuration for them will not be modified.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_admin/wal/properties</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"logfileSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>  <span class="hljs-string">"allowOversizeEntries"</span> : <span class="hljs-literal">true</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"allowOversizeEntries"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"logfileSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>  <span class="hljs-string">"historicLogfiles"</span> : <span class="hljs-number">10</span>, 
  * </code><code>  <span class="hljs-string">"reserveLogfiles"</span> : <span class="hljs-number">3</span>, 
  * </code><code>  <span class="hljs-string">"syncInterval"</span> : <span class="hljs-number">100</span>, 
  * </code><code>  <span class="hljs-string">"throttleWait"</span> : <span class="hljs-number">15000</span>, 
  * </code><code>  <span class="hljs-string">"throttleWhenPending"</span> : <span class="hljs-number">0</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .path(path"/_db/_system/_admin/wal/properties".withArguments(Map()))
    .call[ArangoResponse]
}