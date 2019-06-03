package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiReplicationLoggerStateGet(client: HttpClient) {
  /**
  * Returns the current state of the server's replication logger. The state will
  * include information about whether the logger is running and about the last
  * logged tick value. This tick value is important for incremental fetching of
  * data.
  * 
  * The body of the response contains a JSON object with the following
  * attributes:
  * 
  * - *state*: the current logger state as a JSON object with the following
  *   sub-attributes:
  * 
  *   - *running*: whether or not the logger is running
  * 
  *   - *lastLogTick*: the tick value of the latest tick the logger has logged.
  *     This value can be used for incremental fetching of log data.
  * 
  *   - *totalEvents*: total number of events logged since the server was started.
  *     The value is not reset between multiple stops and re-starts of the logger.
  * 
  *   - *time*: the current date and time on the logger server
  * 
  * - *server*: a JSON object with the following sub-attributes:
  * 
  *   - *version*: the logger server's version
  * 
  *   - *serverId*: the logger server's id
  * 
  * - *clients*: returns the last fetch status by replication clients connected to
  *   the logger. Each client is returned as a JSON object with the following attributes:
  * 
  *   - *serverId*: server id of client
  * 
  *   - *lastServedTick*: last tick value served to this client via the *logger-follow* API
  * 
  *   - *time*: date and time when this client last called the *logger-follow* API
  * 
  * 
  * 
  * 
  * **Example:**
  *  Returns the state of the replication logger.
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/replication/logger-state</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"state"</span> : { 
  * </code><code>    <span class="hljs-string">"running"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"lastLogTick"</span> : <span class="hljs-string">"105067"</span>, 
  * </code><code>    <span class="hljs-string">"lastUncommittedLogTick"</span> : <span class="hljs-string">"105067"</span>, 
  * </code><code>    <span class="hljs-string">"totalEvents"</span> : <span class="hljs-number">35288</span>, 
  * </code><code>    <span class="hljs-string">"time"</span> : <span class="hljs-string">"2019-02-20T10:33:10Z"</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"server"</span> : { 
  * </code><code>    <span class="hljs-string">"version"</span> : <span class="hljs-string">"3.5.0-devel"</span>, 
  * </code><code>    <span class="hljs-string">"serverId"</span> : <span class="hljs-string">"153018529730512"</span>, 
  * </code><code>    <span class="hljs-string">"engine"</span> : <span class="hljs-string">"mmfiles"</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"clients"</span> : [ ] 
  * </code><code>}
  * </code></pre>
  */
  def get(): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .call[ArangoResponse]
}