package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiTasks{Id}Put(client: HttpClient) {
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
  def put(id: String, body: PutAPINewTasks): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .params("id" -> id.toString)
    .restful[PutAPINewTasks, ArangoResponse](body)
}