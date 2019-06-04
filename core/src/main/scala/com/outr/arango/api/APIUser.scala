package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APIUser(client: HttpClient) {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **passwd**: The user password as a string. If no password is specified, the empty string
  *    will be used. If you pass the special value *ARANGODB_DEFAULT_ROOT_PASSWORD*,
  *    then the password will be set the value stored in the environment variable
  *    `ARANGODB_DEFAULT_ROOT_PASSWORD`. This can be used to pass an instance
  *    variable into ArangoDB. For example, the instance identifier from Amazon.
  *   - **active**: An optional flag that specifies whether the user is active.  If not
  *    specified, this will default to true
  *   - **user**: The name of the user as a string. This is mandatory.
  *   - **extra**: An optional JSON object with arbitrary extra data about the user.
  * 
  * 
  * 
  * 
  * Create a new user. You need server access level *Administrate* in order to
  * execute this REST call.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/user</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"user"</span> : <span class="hljs-string">"admin@example"</span>, 
  * </code><code>  <span class="hljs-string">"passwd"</span> : <span class="hljs-string">"secure"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"user"</span> : <span class="hljs-string">"admin@example"</span>, 
  * </code><code>  <span class="hljs-string">"active"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"extra"</span> : { 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * 
  * <!-- ---------------------------------------------------------------------- -->
  */
  def post(body: UserHandlingCreate): Future[ArangoResponse] = client
    .method(HttpMethod.Post)
    .path(path"/_db/_system/_api/user".withArguments(Map()))
    .restful[UserHandlingCreate, ArangoResponse](body)

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
    .path(path"/_db/_system/_api/user/".withArguments(Map()))
    .call[ArangoResponse]
}