package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIAnalyzer(client: HttpClient) {
  /**
  * Retrieves a an array of all analyzer definitions.
  * The resulting array contains objects with the following attributes:
  * - *name*: the analyzer name
  * - *type*: the analyzer type
  * - *properties*: the properties used to configure the specified type
  * - *features*: the set of features to set on the analyzer generated fields
  * 
  * 
  * 
  * 
  * **Example:**
  *  Retrieve all analyzer definitions:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/analyzer</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"result"</span> : [ ] 
  * </code><code>}
  * </code></pre>
  */
  def get(): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/analyzer", append = true) 
    .call[Json]

  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **features** (string): The set of features to set on the analyzer generated fields.
  *    The default value is an empty array.
  *   - **type**: The analyzer type.
  *   - **name**: The analyzer name.
  *   - **properties**: The properties used to configure the specified type.
  *    Value may be a string, an object or null.
  *    The default value is *null*.
  * 
  * 
  * 
  * 
  * Creates a new analyzer based on the provided configuration.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/analyzer</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"_system::testAnalyzer"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"identity"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"_system::testAnalyzer"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"identity"</span>, 
  * </code><code>  <span class="hljs-string">"properties"</span> : <span class="hljs-literal">null</span>, 
  * </code><code>  <span class="hljs-string">"features"</span> : [ ] 
  * </code><code>}
  * </code></pre>
  */
  def post(body: PostAPIAnalyzer): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/analyzer", append = true) 
    .restful[PostAPIAnalyzer, Json](body)
}