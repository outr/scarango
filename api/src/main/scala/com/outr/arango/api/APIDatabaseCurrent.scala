package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIDatabaseCurrent {
  /**
  * Retrieves information about the current database
  * 
  * The response is a JSON object with the following attributes:
  * 
  * - *name*: the name of the current database
  * 
  * - *id*: the id of the current database
  * 
  * - *path*: the filesystem path of the current database
  * 
  * - *isSystem*: whether or not the current database is the *_system* database
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/database/current</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"result"</span> : { 
  * </code><code>    <span class="hljs-string">"name"</span> : <span class="hljs-string">"_system"</span>, 
  * </code><code>    <span class="hljs-string">"id"</span> : <span class="hljs-string">"1"</span>, 
  * </code><code>    <span class="hljs-string">"path"</span> : <span class="hljs-string">"/tmp/arangosh_uprJb4/tmp-27793-56941049/data/databases/database-1"</span>, 
  * </code><code>    <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def get(client: HttpClient)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/database/current", append = true) 
    .call[Json]
}