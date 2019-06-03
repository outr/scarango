package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiUserGet(client: HttpClient) {
  /**
  * Fetches data about all users.  You need the *Administrate* server access level
  * in order to execute this REST call.  Otherwise, you will only get information
  * about yourself.
  * 
  * The call will return a JSON object with at least the following
  * attributes on success:
  * 
  * - *user*: The name of the user as a string.
  * - *active*: An optional flag that specifies whether the user is active.
  * - *extra*: An optional JSON object with arbitrary extra data about the user.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/user</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"result"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"user"</span> : <span class="hljs-string">"tester"</span>, 
  * </code><code>      <span class="hljs-string">"active"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>      <span class="hljs-string">"extra"</span> : { 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"user"</span> : <span class="hljs-string">"admin"</span>, 
  * </code><code>      <span class="hljs-string">"active"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"extra"</span> : { 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"user"</span> : <span class="hljs-string">"root"</span>, 
  * </code><code>      <span class="hljs-string">"active"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"extra"</span> : { 
  * </code><code>      } 
  * </code><code>    } 
  * </code><code>  ] 
  * </code><code>}
  * </code></pre>
  */
  def get(): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .call[ArangoResponse]
}