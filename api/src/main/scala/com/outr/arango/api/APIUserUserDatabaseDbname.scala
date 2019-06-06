package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
object APIUserUserDatabaseDbname {
  /**
  * Clears the database access level for the database *dbname* of user *user*. As
  * consequence the default database access level is used. If there is no defined
  * default database access level, it defaults to *No access*. You need permission
  * to the *_system* database in order to execute this REST call.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/user/admin@myapp/database/_system</span>
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
  def delete(client: HttpClient, user: String, dbname: String): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/user/{user}/database/{dbname}".withArguments(Map("user" -> user, "dbname" -> dbname)), append = true)
    .call[Json]

  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **grant**: Use "rw" to set the database access level to *Administrate* .
  *    Use "ro" to set the database access level to *Access*.
  *    Use "none" to set the database access level to *No access*.
  * 
  * 
  * 
  * 
  * Sets the database access levels for the database *dbname* of user *user*. You
  * need the *Administrate* server access level in order to execute this REST
  * call.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/user/admin@myapp/database/_system</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"grant"</span> : <span class="hljs-string">"rw"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_system"</span> : <span class="hljs-string">"rw"</span>, 
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
  def put(client: HttpClient, body: UserHandlingGrantDatabase, user: String, dbname: String): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/user/{user}/database/{dbname}".withArguments(Map("user" -> user, "dbname" -> dbname)), append = true)
    .restful[UserHandlingGrantDatabase, Json](body)
}