package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiUser{User}Database{Dbname}{Collection}Delete(client: HttpClient) {
  /**
  * Clears the collection access level for the collection *collection* in the
  * database *dbname* of user *user*.  As consequence the default collection
  * access level is used. If there is no defined default collection access level,
  * it defaults to *No access*.  You need permissions to the *_system* database in
  * order to execute this REST call.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/user/admin@myapp/database/_system/reports</span>
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
  def delete(user: String, dbname: String, collection: String): Future[ArangoResponse] = client
    .method(HttpMethod.Delete)
    .params("user" -> user.toString)
    .params("dbname" -> dbname.toString)
    .params("collection" -> collection.toString)
    .call[ArangoResponse]
}