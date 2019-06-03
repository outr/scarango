package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiUser{User}DatabaseGet(client: HttpClient) {
  /**
  * Fetch the list of databases available to the specified *user*. You need
  * *Administrate* for the server access level in order to execute this REST call.
  * 
  * The call will return a JSON object with the per-database access
  * privileges for the specified user. The *result* object will contain
  * the databases names as object keys, and the associated privileges
  * for the database as values.
  * 
  * In case you specified *full*, the result will contain the permissions
  * for the databases as well as the permissions for the collections.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/user/anotherAdmin@secapp/database/</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"result"</span> : { 
  * </code><code>    <span class="hljs-string">"_system"</span> : <span class="hljs-string">"rw"</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  With the full response format:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/user/anotherAdmin@secapp/database?full=<span class="hljs-literal">true</span></span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"result"</span> : { 
  * </code><code>    <span class="hljs-string">"_system"</span> : { 
  * </code><code>      <span class="hljs-string">"permission"</span> : <span class="hljs-string">"rw"</span>, 
  * </code><code>      <span class="hljs-string">"collections"</span> : { 
  * </code><code>        <span class="hljs-string">"_queues"</span> : <span class="hljs-string">"undefined"</span>, 
  * </code><code>        <span class="hljs-string">"_frontend"</span> : <span class="hljs-string">"undefined"</span>, 
  * </code><code>        <span class="hljs-string">"_appbundles"</span> : <span class="hljs-string">"undefined"</span>, 
  * </code><code>        <span class="hljs-string">"_statistics"</span> : <span class="hljs-string">"undefined"</span>, 
  * </code><code>        <span class="hljs-string">"_users"</span> : <span class="hljs-string">"undefined"</span>, 
  * </code><code>        <span class="hljs-string">"_iresearch_analyzers"</span> : <span class="hljs-string">"undefined"</span>, 
  * </code><code>        <span class="hljs-string">"_jobs"</span> : <span class="hljs-string">"undefined"</span>, 
  * </code><code>        <span class="hljs-string">"demo"</span> : <span class="hljs-string">"undefined"</span>, 
  * </code><code>        <span class="hljs-string">"_aqlfunctions"</span> : <span class="hljs-string">"undefined"</span>, 
  * </code><code>        <span class="hljs-string">"_graphs"</span> : <span class="hljs-string">"undefined"</span>, 
  * </code><code>        <span class="hljs-string">"_apps"</span> : <span class="hljs-string">"undefined"</span>, 
  * </code><code>        <span class="hljs-string">"_statisticsRaw"</span> : <span class="hljs-string">"undefined"</span>, 
  * </code><code>        <span class="hljs-string">"_statistics15"</span> : <span class="hljs-string">"undefined"</span>, 
  * </code><code>        <span class="hljs-string">"animals"</span> : <span class="hljs-string">"undefined"</span>, 
  * </code><code>        <span class="hljs-string">"*"</span> : <span class="hljs-string">"undefined"</span> 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"*"</span> : { 
  * </code><code>      <span class="hljs-string">"permission"</span> : <span class="hljs-string">"none"</span> 
  * </code><code>    } 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * 
  * <!-- ---------------------------------------------------------------------- -->
  */
  def get(user: String, full: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .params("user" -> user.toString)
    .param[Option[Boolean]]("full", full, None)
    .call[ArangoResponse]
}