package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIEndpoint {
  /**
  * Returns an array of all configured endpoints the server is listening on.
  * 
  * The result is a JSON array of JSON objects, each with `"entrypoint"' as
  * the only attribute, and with the value being a string describing the
  * endpoint.
  * 
  * **Note**: retrieving the array of all endpoints is allowed in the system database
  * only. Calling this action in any other database will make the server return
  * an error.
  * 
  * 
  * <!-- Hints Start -->
  * 
  * **Warning:**  
  * This route should no longer be used.
  * It is considered as deprecated from version 3.4.0 on.
  * 
  * 
  * 
  * <!-- Hints End -->
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/endpoint</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>[ 
  * </code><code>  { 
  * </code><code>    <span class="hljs-string">"endpoint"</span> : <span class="hljs-string">"http://127.0.0.1:18836"</span> 
  * </code><code>  } 
  * </code><code>]
  * </code></pre>
  */
  def get(client: HttpClient)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/endpoint", append = true) 
    .call[Json]
}