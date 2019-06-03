package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiAqlfunctionPost(client: HttpClient) {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **isDeterministic**: an optional boolean value to indicate whether the function
  *    results are fully deterministic (function return value solely depends on
  *    the input value and return value is the same for repeated calls with same
  *    input). The *isDeterministic* attribute is currently not used but may be
  *    used later for optimizations.
  *   - **code**: a string representation of the function body.
  *   - **name**: the fully qualified name of the user functions.
  * 
  * 
  * 
  * 
  * 
  * In case of success, HTTP 200 is returned.
  * If the function isn't valid etc. HTTP 400 including a detailed error message will be returned.
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * If the function already existed and was replaced by the
  * call, the server will respond with *HTTP 200*.
  * 
  * - **code**: the HTTP status code
  * - **isNewlyCreated**: boolean flag to indicate whether the function was newly created (*false* in this case)
  * - **error**: boolean flag to indicate whether an error occurred (*false* in this case)
  * 
  * 
  * **HTTP 201**
  * *A json document with these Properties is returned:*
  * 
  * If the function can be registered by the server, the server will respond with
  * *HTTP 201*.
  * 
  * - **code**: the HTTP status code
  * - **isNewlyCreated**: boolean flag to indicate whether the function was newly created (*true* in this case)
  * - **error**: boolean flag to indicate whether an error occurred (*false* in this case)
  * 
  * 
  * **HTTP 400**
  * *A json document with these Properties is returned:*
  * 
  * If the JSON representation is malformed or mandatory data is missing from the
  * request, the server will respond with *HTTP 400*.
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
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/aqlfunction</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"myfunctions::temperature::celsiustofahrenheit"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-string">"function (celsius) { return celsius * 1.8 + 32; }"</span>, 
  * </code><code>  <span class="hljs-string">"isDeterministic"</span> : <span class="hljs-literal">true</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span>, 
  * </code><code>  <span class="hljs-string">"isNewlyCreated"</span> : <span class="hljs-literal">true</span> 
  * </code><code>}
  * </code></pre>
  */
  def post(body: PostAPIAqlfunction): Future[PostAPIAqlfunctionRc200] = client
    .method(HttpMethod.Post)
    .restful[PostAPIAqlfunction, PostAPIAqlfunctionRc200](body)
}