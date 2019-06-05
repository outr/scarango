package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIDatabaseDatabaseName(client: HttpClient) {
  /**
  * Drops the database along with all data stored in it.
  * 
  * **Note**: dropping a database is only possible from within the *_system* database.
  * The *_system* database itself cannot be dropped.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/database/example</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"result"</span> : <span class="hljs-literal">true</span> 
  * </code><code>}
  * </code></pre>
  */
  def delete(databaseName: String): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/database/{database-name}".withArguments(Map("database-name" -> databaseName)), append = true)
    .call[Json]
}