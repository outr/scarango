package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiAnalyzer{AnalyzerName}Delete(client: HttpClient) {
  /**
  * Removes an analyzer configuration identified by *analyzer-name*.
  * 
  * If the analyzer definition was successfully dropped, an object is returned with
  * the following attributes:
  * - *error*: *false*
  * - *name*: The name of the removed analyzer
  * 
  * 
  * 
  * 
  * **Example:**
  *  Removing without *force*:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/analyzer/_system%3A%3AtestAnalyzer</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"_system::testAnalyzer"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Removing with *force*:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/collection</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"testCollection"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/view</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"testView"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"arangosearch"</span>, 
  * </code><code>  <span class="hljs-string">"links"</span> : { 
  * </code><code>    <span class="hljs-string">"testCollection"</span> : { 
  * </code><code>      <span class="hljs-string">"analyzers"</span> : [ 
  * </code><code>        <span class="hljs-string">"_system::testAnalyzer"</span> 
  * </code><code>      ] 
  * </code><code>    } 
  * </code><code>  } 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/analyzer/_system%3A%3AtestAnalyzer?force=<span class="hljs-literal">false</span></span>
  * </code><code>
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/analyzer/_system%3A%3AtestAnalyzer?force=<span class="hljs-literal">true</span></span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"_system::testAnalyzer"</span> 
  * </code><code>}
  * </code></pre>
  */
  def delete(analyzerName: String, force: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Delete)
    .params("analyzer-name" -> analyzer-name.toString)
    .param[Option[Boolean]]("force", force, None)
    .call[ArangoResponse]
}