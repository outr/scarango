package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiAnalyzerPost(client: HttpClient) {
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
  def post(body: PostAPIAnalyzer): Future[ArangoResponse] = client
    .method(HttpMethod.Post)
    .restful[PostAPIAnalyzer, ArangoResponse](body)
}