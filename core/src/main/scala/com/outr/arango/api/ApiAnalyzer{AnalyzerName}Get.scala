package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiAnalyzer{AnalyzerName}Get(client: HttpClient) {
  /**
  * Retrieves the full definition for the specified analyzer name.
  * The resulting object contains the following attributes:
  * - *name*: the analyzer name
  * - *type*: the analyzer type
  * - *properties*: the properties used to configure the specified type
  * - *features*: the set of features to set on the analyzer generated fields
  * 
  * 
  * 
  * 
  * **Example:**
  *  Retrieve an analyzer definition:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/analyzer/_system%3A%3AtestAnalyzer</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"identity"</span>, 
  * </code><code>  <span class="hljs-string">"properties"</span> : <span class="hljs-string">"test properties"</span>, 
  * </code><code>  <span class="hljs-string">"features"</span> : [ ], 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"_system::testAnalyzer"</span> 
  * </code><code>}
  * </code></pre>
  */
  def get(analyzerName: String): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .params("analyzer-name" -> analyzer-name.toString)
    .call[ArangoResponse]
}