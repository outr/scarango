package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIAqlfunctionName(client: HttpClient) {
  /**
  * Removes an existing AQL user function or function group, identified by *name*.
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * If the function can be removed by the server, the server will respond with
  * *HTTP 200*.
  * 
  * - **deletedCount**: The number of deleted user functions, always `1` when `group` is set to *false*. 
  * Any number `>= 0` when `group` is set to *true*
  * - **code**: the HTTP status code
  * - **error**: boolean flag to indicate whether an error occurred (*false* in this case)
  * 
  * 
  * **HTTP 400**
  * *A json document with these Properties is returned:*
  * 
  * If the user function name is malformed, the server will respond with *HTTP 400*.
  * 
  * - **errorMessage**: a descriptive error message
  * - **errorNum**: the server error number
  * - **code**: the HTTP status code
  * - **error**: boolean flag to indicate whether an error occurred (*true* in this case)
  * 
  * 
  * **HTTP 404**
  * *A json document with these Properties is returned:*
  * 
  * If the specified user user function does not exist, the server will respond with *HTTP 404*.
  * 
  * - **errorMessage**: a descriptive error message
  * - **errorNum**: the server error number
  * - **code**: the HTTP status code
  * - **error**: boolean flag to indicate whether an error occurred (*true* in this case)
  * 
  * 
  * 
  * 
  * **Example:**
  *  deletes a function:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/aqlfunction/square::x::y</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"deletedCount"</span> : <span class="hljs-number">1</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  function not found:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/aqlfunction/myfunction::x::y</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Not Found
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"no AQL user function with name 'myfunction::x::y' found"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">404</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1582</span> 
  * </code><code>}
  * </code></pre>
  */
  def delete(name: String, group: Option[String] = None): Future[DeleteAPIAqlfunctionRc200] = client
    .method(HttpMethod.Delete)
    .path(path"/_db/_system/_api/aqlfunction/{name}".withArguments(Map("name" -> name)))
    .param[Option[String]]("group", group, None)
    .call[DeleteAPIAqlfunctionRc200]
}