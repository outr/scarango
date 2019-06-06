package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
object APIReplicationApplierStart {
  /**
  * Starts the replication applier. This will return immediately if the
  * replication applier is already running.
  * 
  * If the replication applier is not already running, the applier configuration
  * will be checked, and if it is complete, the applier will be started in a
  * background thread. This means that even if the applier will encounter any
  * errors while running, they will not be reported in the response to this
  * method.
  * 
  * To detect replication applier errors after the applier was started, use the
  * {@literal *}/_api/replication/applier-state* API instead.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/replication/applier-start</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"state"</span> : { 
  * </code><code>    <span class="hljs-string">"running"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"phase"</span> : <span class="hljs-string">"running"</span>, 
  * </code><code>    <span class="hljs-string">"lastAppliedContinuousTick"</span> : <span class="hljs-literal">null</span>, 
  * </code><code>    <span class="hljs-string">"lastProcessedContinuousTick"</span> : <span class="hljs-literal">null</span>, 
  * </code><code>    <span class="hljs-string">"lastAvailableContinuousTick"</span> : <span class="hljs-literal">null</span>, 
  * </code><code>    <span class="hljs-string">"safeResumeTick"</span> : <span class="hljs-literal">null</span>, 
  * </code><code>    <span class="hljs-string">"ticksBehind"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"progress"</span> : { 
  * </code><code>      <span class="hljs-string">"time"</span> : <span class="hljs-string">"2019-02-20T10:32:42Z"</span>, 
  * </code><code>      <span class="hljs-string">"message"</span> : <span class="hljs-string">"applier initially created for database '_system'"</span>, 
  * </code><code>      <span class="hljs-string">"failedConnects"</span> : <span class="hljs-number">0</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"totalRequests"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"totalFailedConnects"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"totalEvents"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"totalResyncs"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"totalOperationsExcluded"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"lastError"</span> : { 
  * </code><code>      <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">0</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"time"</span> : <span class="hljs-string">"2019-02-20T10:33:03Z"</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"server"</span> : { 
  * </code><code>    <span class="hljs-string">"version"</span> : <span class="hljs-string">"3.5.0-devel"</span>, 
  * </code><code>    <span class="hljs-string">"serverId"</span> : <span class="hljs-string">"153018529730512"</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"endpoint"</span> : <span class="hljs-string">"tcp://127.0.0.1:8529"</span>, 
  * </code><code>  <span class="hljs-string">"database"</span> : <span class="hljs-string">"_system"</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(client: HttpClient, from: Option[String] = None): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/replication/applier-start", append = true) 
    .param[Option[String]]("from", from, None)
    .call[Json]
}