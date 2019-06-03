package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiUser{User}Get(client: HttpClient) {
  /**
  * Fetches data about the specified user. You can fetch information about
  * yourself or you need the *Administrate* server access level in order to
  * execute this REST call.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/user/admin@myapp</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"user"</span> : <span class="hljs-string">"admin@myapp"</span>, 
  * </code><code>  <span class="hljs-string">"active"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"extra"</span> : { 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * 
  * <!-- ---------------------------------------------------------------------- -->
  */
  def get(user: String): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .params("user" -> user.toString)
    .call[ArangoResponse]
}