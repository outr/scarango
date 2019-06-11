package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIReplicationLoggerFirstTick {
  /**
  * Returns the first available tick value that can be served from the server's
  * replication log. This method can be called by replication clients after to
  * determine if certain data (identified by a tick value) is still available
  * for replication.
  * 
  * The result is a JSON object containing the attribute *firstTick*. This
  * attribute contains the minimum tick value available in the server's
  * replication
  * log.
  * 
  * **Note**: this method is not supported on a coordinator in a cluster.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Returning the first available tick
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/replication/logger-first-tick</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"firstTick"</span> : <span class="hljs-string">"5"</span> 
  * </code><code>}
  * </code></pre>
  */
  def get(client: HttpClient)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/replication/logger-first-tick", append = true) 
    .call[Json]
}