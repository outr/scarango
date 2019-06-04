package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APIWalLastTick(client: HttpClient) {
  /**
  * Returns the last available tick value that can be served from the server's
  * replication log. This corresponds to the tick of the latest successfull operation.
  * 
  * The result is a JSON object containing the attributes *tick*, *time* and *server*. 
  * * *tick*: contains the last available tick, *time* 
  * * *time*: the server time as string in format "YYYY-MM-DDTHH:MM:SSZ"
  * * *server*: An object with fields *version* and *serverId*
  * 
  * **Note**: this method is not supported on a coordinator in a cluster.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Returning the first available tick
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/wal/lastTick</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"time"</span> : <span class="hljs-string">"2019-02-20T10:33:12Z"</span>, 
  * </code><code>  <span class="hljs-string">"tick"</span> : <span class="hljs-string">"107352"</span>, 
  * </code><code>  <span class="hljs-string">"server"</span> : { 
  * </code><code>    <span class="hljs-string">"version"</span> : <span class="hljs-string">"3.5.0-devel"</span>, 
  * </code><code>    <span class="hljs-string">"serverId"</span> : <span class="hljs-string">"153018529730512"</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def get(): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_api/wal/lastTick".withArguments(Map()))
    .call[ArangoResponse]
}