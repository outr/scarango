package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiTasks{Id}Get(client: HttpClient) {
  /**
  * fetches one existing task on the server specified by *id*
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * The requested task
  * 
  * - **name**: The fully qualified name of the user function
  * - **created**: The timestamp when this task was created
  * - **database**: the database this task belongs to
  * - **period**: this task should run each `period` seconds
  * - **command**: the javascript function for this task
  * - **offset**: time offset in seconds from the created timestamp
  * - **type**: What type of task is this [ `periodic`, `timed`]
  *  - periodic are tasks that repeat periodically
  *  - timed are tasks that execute once at a specific time
  * - **id**: A string identifying the task
  * 
  * 
  * 
  * 
  * **Example:**
  *  Fetching a single task by its id
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/tasks</span> &lt;&lt;EOF
  * </code><code>{"id":"testTask","command":"console.log('Hello from task!');","offset":10000}
  * </code><code>EOF
  * </code><code>
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/tasks/testTask</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"testTask"</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"user-defined task"</span>, 
  * </code><code>  <span class="hljs-string">"created"</span> : <span class="hljs-number">1550658791.3349102</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"timed"</span>, 
  * </code><code>  <span class="hljs-string">"offset"</span> : <span class="hljs-number">10000</span>, 
  * </code><code>  <span class="hljs-string">"command"</span> : <span class="hljs-string">"(function (params) { console.log('Hello from task!'); } )(params);"</span>, 
  * </code><code>  <span class="hljs-string">"database"</span> : <span class="hljs-string">"_system"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Trying to fetch a non-existing task
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/tasks/non-existing-task</span>
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
  */
  def get(id: String): Future[APITaskStruct] = client
    .method(HttpMethod.Get)
    .params("id" -> id.toString)
    .call[APITaskStruct]
}