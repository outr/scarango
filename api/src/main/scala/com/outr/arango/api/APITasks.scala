package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APITasks {
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
  * creates a new task with a generated id
  * 
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * The task was registered
  * 
  * - **code**: The status code, 200 in this case.
  * - **created**: The timestamp when this task was created
  * - **database**: the database this task belongs to
  * - **period**: this task should run each `period` seconds
  * - **command**: the javascript function for this task
  * - **error**: *false* in this case
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
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/tasks/</span> &lt;&lt;EOF
  * </code><code>{ 
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
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"105969"</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"SampleTask"</span>, 
  * </code><code>  <span class="hljs-string">"created"</span> : <span class="hljs-number">1550658791.3249488</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"periodic"</span>, 
  * </code><code>  <span class="hljs-string">"period"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"offset"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"command"</span> : <span class="hljs-string">"(function (params) { (function(params) { require('@arangodb').print(params); })(params) } )(params);"</span>, 
  * </code><code>  <span class="hljs-string">"database"</span> : <span class="hljs-string">"_system"</span> 
  * </code><code>}
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/tasks/105969</span>
  * </code><code>
  * </code></pre>
  */
  def post(client: HttpClient, body: PostAPINewTasks)(implicit ec: ExecutionContext): Future[PostAPINewTasksRc200] = client
    .method(HttpMethod.Post)
    .path(path"/_api/tasks", append = true) 
    .restful[PostAPINewTasks, PostAPINewTasksRc200](body)

  /**
  * fetches all existing tasks on the server
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * The list of tasks
  * 
  * [
  *   - **name**: The fully qualified name of the user function
  *   - **created**: The timestamp when this task was created
  *   - **database**: the database this task belongs to
  *   - **period**: this task should run each `period` seconds
  *   - **command**: the javascript function for this task
  *   - **offset**: time offset in seconds from the created timestamp
  *   - **type**: What type of task is this [ `periodic`, `timed`]
  *     - periodic are tasks that repeat periodically
  *     - timed are tasks that execute once at a specific time
  *   - **id**: A string identifying the task
  * ]
  * 
  * 
  * 
  * 
  * **Example:**
  *  Fetching all tasks
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/tasks</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>[ 
  * </code><code>  { 
  * </code><code>    <span class="hljs-string">"id"</span> : <span class="hljs-string">"55"</span>, 
  * </code><code>    <span class="hljs-string">"name"</span> : <span class="hljs-string">"user-defined task"</span>, 
  * </code><code>    <span class="hljs-string">"created"</span> : <span class="hljs-number">1550658763.4988394</span>, 
  * </code><code>    <span class="hljs-string">"type"</span> : <span class="hljs-string">"periodic"</span>, 
  * </code><code>    <span class="hljs-string">"period"</span> : <span class="hljs-number">1</span>, 
  * </code><code>    <span class="hljs-string">"offset"</span> : <span class="hljs-number">0.000001</span>, 
  * </code><code>    <span class="hljs-string">"command"</span> : <span class="hljs-string">"(function (params) { (function () {\n        require('@arangodb/foxx/queues/manager').manage();\n      })(params) } )(params);"</span>, 
  * </code><code>    <span class="hljs-string">"database"</span> : <span class="hljs-string">"_system"</span> 
  * </code><code>  } 
  * </code><code>]
  * </code></pre>
  */
  def get(client: HttpClient)(implicit ec: ExecutionContext): Future[GetAPITasksAllRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_api/tasks/", append = true) 
    .call[GetAPITasksAllRc200]
}