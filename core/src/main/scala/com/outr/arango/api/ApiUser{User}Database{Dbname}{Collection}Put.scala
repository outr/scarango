package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiUser{User}Database{Dbname}{Collection}Put(client: HttpClient) {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **grant**: Use "rw" to set the collection level access to *Read/Write*.
  *    Use "ro" to set the collection level access to  *Read Only*.
  *    Use "none" to set the collection level access to *No access*.
  * 
  * 
  * 
  * 
  * Sets the collection access level for the *collection* in the database *dbname*
  * for user *user*. You need the *Administrate* server access level in order to
  * execute this REST call.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/user/admin@myapp/database/_system/reports</span> &lt;&lt;EOF
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
  * </code><code>  <span class="hljs-string">"_system/reports"</span> : <span class="hljs-string">"rw"</span>, 
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
  def put(body: UserHandlingGrantCollection, user: String, dbname: String, collection: String): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .params("user" -> user.toString)
    .params("dbname" -> dbname.toString)
    .params("collection" -> collection.toString)
    .restful[UserHandlingGrantCollection, ArangoResponse](body)
}