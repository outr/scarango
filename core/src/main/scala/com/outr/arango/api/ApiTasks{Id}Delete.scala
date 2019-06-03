package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiTasks{Id}Delete(client: HttpClient) {
  /**
  * Deletes the task identified by *id* on the server. 
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * If the task was deleted, *HTTP 200* is returned.
  * 
  * - **code**: The status code, 200 in this case.
  * - **error**: *false* in this case
  * 
  * 
  * **HTTP 404**
  * *A json document with these Properties is returned:*
  * 
  * If the task *id* is unknown, then an *HTTP 404* is returned.
  * 
  * - **errorMessage**: A plain text message stating what went wrong.
  * - **code**: The status code, 404 in this case.
  * - **error**: *true* in this case
  * 
  * 
  * 
  * 
  * **Example:**
  *  trying to delete non existing task
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/tasks/NoTaskWithThatName</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Not Found
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"task not found"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">404</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1852</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Remove existing Task
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/tasks/SampleTask</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"result"</span> : <span class="hljs-string">"(non-representable type)"</span> 
  * </code><code>}
  * </code></pre>
  */
  def delete(id: String): Future[DeleteAPITasksRc200] = client
    .method(HttpMethod.Delete)
    .params("id" -> id.toString)
    .call[DeleteAPITasksRc200]
}