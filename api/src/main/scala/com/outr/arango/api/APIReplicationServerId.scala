package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIReplicationServerId {
  /**
  * Returns the servers id. The id is also returned by other replication API
  * methods, and this method is an easy means of determining a server's id.
  * 
  * The body of the response is a JSON object with the attribute *serverId*. The
  * server id is returned as a string.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/replication/server-id</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"serverId"</span> : <span class="hljs-string">"153018529730512"</span> 
  * </code><code>}
  * </code></pre>
  */
  def get(client: HttpClient)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/replication/server-id", append = true) 
    .call[Json]
}