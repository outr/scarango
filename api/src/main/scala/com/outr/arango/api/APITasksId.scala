package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APITasksId {
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
  def delete(client: HttpClient, id: String)(implicit ec: ExecutionContext): Future[DeleteAPITasksRc200] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/tasks/{id}".withArguments(Map("id" -> id)), append = true)
    .call[DeleteAPITasksRc200]

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
  def get(client: HttpClient, id: String)(implicit ec: ExecutionContext): Future[APITaskStruct] = client
    .method(HttpMethod.Get)
    .path(path"/_api/tasks/{id}".withArguments(Map("id" -> id)), append = true)
    .call[APITaskStruct]

  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **params**: The parameters to be passed into command
  *   - **offset**: Number of seconds initial delay
  *   - **command**: The JavaScript code to be executed
  *   - **name**: The name of the task
  *   - **period**: number of seconds between the executions
  * 
  * 
  * 
  * 
  * registers a new task with the specified id
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/tasks/sampleTask</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"SampleTask"</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"SampleTask"</span>, 
  * </code><code>  <span class="hljs-string">"command"</span> : <span class="hljs-string">"(function(params) { require('@arangodb').print(params); })(params)"</span>, 
  * </code><code>  <span class="hljs-string">"params"</span> : { 
  * </code><code>    <span class="hljs-string">"foo"</span> : <span class="hljs-string">"bar"</span>, 
  * </code><code>    <span class="hljs-string">"bar"</span> : <span class="hljs-string">"foo"</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"period"</span> : <span class="hljs-number">2</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"sampleTask"</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"SampleTask"</span>, 
  * </code><code>  <span class="hljs-string">"created"</span> : <span class="hljs-number">1550658791.336974</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"periodic"</span>, 
  * </code><code>  <span class="hljs-string">"period"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"offset"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"command"</span> : <span class="hljs-string">"(function (params) { (function(params) { require('@arangodb').print(params); })(params) } )(params);"</span>, 
  * </code><code>  <span class="hljs-string">"database"</span> : <span class="hljs-string">"_system"</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(client: HttpClient, id: String, body: PutAPINewTasks)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/tasks/{id}".withArguments(Map("id" -> id)), append = true)
    .restful[PutAPINewTasks, Json](body)
}