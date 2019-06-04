package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIUserUser(client: HttpClient) {
  /**
  * Removes an existing user, identified by *user*.  You need *Administrate* for
  * the server access level in order to execute this REST call.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/user/userToDelete@myapp</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Accepted
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">202</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * 
  * <!-- ---------------------------------------------------------------------- -->
  */
  def delete(user: String): Future[ArangoResponse] = client
    .method(HttpMethod.Delete)
    .path(path"/_db/_system/_api/user/{user}".withArguments(Map("user" -> user)))
    .call[ArangoResponse]

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
    .path(path"/_db/_system/_api/user/{user}".withArguments(Map("user" -> user)))
    .call[ArangoResponse]

  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **passwd**: The user password as a string. Specifying a password is mandatory, but
  *    the empty string is allowed for passwords
  *   - **active**: An optional flag that specifies whether the user is active.  If not
  *    specified, this will default to true
  *   - **extra**: An optional JSON object with arbitrary extra data about the user.
  * 
  * 
  * 
  * 
  * Partially updates the data of an existing user. The name of an existing user
  * must be specified in *user*. You need server access level *Administrate* in
  * order to execute this REST call. Additionally, a user can change his/her own
  * data.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PATCH --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/user/admin@myapp</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"passwd"</span> : <span class="hljs-string">"secure"</span> 
  * </code><code>}
  * </code><code>EOF
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
  * 
  * 
  * <!-- ---------------------------------------------------------------------- -->
  */
  def patch(user: String, body: UserHandlingModify): Future[ArangoResponse] = client
    .method(HttpMethod.Patch)
    .path(path"/_db/_system/_api/user/{user}".withArguments(Map("user" -> user)))
    .restful[UserHandlingModify, ArangoResponse](body)

  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **passwd**: The user password as a string. Specifying a password is mandatory, but
  *    the empty string is allowed for passwords
  *   - **active**: An optional flag that specifies whether the user is active.  If not
  *    specified, this will default to true
  *   - **extra**: An optional JSON object with arbitrary extra data about the user.
  * 
  * 
  * 
  * 
  * Replaces the data of an existing user. The name of an existing user must be
  * specified in *user*. You need server access level *Administrate* in order to
  * execute this REST call. Additionally, a user can change his/her own data.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/user/admin@myapp</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"passwd"</span> : <span class="hljs-string">"secure"</span> 
  * </code><code>}
  * </code><code>EOF
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
  def put(user: String, body: UserHandlingReplace): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .path(path"/_db/_system/_api/user/{user}".withArguments(Map("user" -> user)))
    .restful[UserHandlingReplace, ArangoResponse](body)
}