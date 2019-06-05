package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIIndexttl(client: HttpClient) {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **fields** (string): an array with exactly one attribute path.
  *   - **type**: must be equal to *"ttl"*.
  *   - **expireAfter**: The time (in seconds) after a document's creation after which the
  *    documents count as "expired".
  * 
  * 
  * 
  * 
  * **NOTE** Swagger examples won't work due to the anchor.
  * 
  * Creates a TTL index for the collection *collection-name* if it
  * does not already exist. The call expects an object containing the index
  * details.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Creating a TTL index
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/index?collection=sessions</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"ttl"</span>, 
  * </code><code>  <span class="hljs-string">"expireAfter"</span> : <span class="hljs-number">3600</span>, 
  * </code><code>  <span class="hljs-string">"fields"</span> : [ 
  * </code><code>    <span class="hljs-string">"createdAt"</span> 
  * </code><code>  ] 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"expireAfter"</span> : <span class="hljs-number">3600</span>, 
  * </code><code>  <span class="hljs-string">"fields"</span> : [ 
  * </code><code>    <span class="hljs-string">"createdAt"</span> 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"sessions/104830"</span>, 
  * </code><code>  <span class="hljs-string">"isNewlyCreated"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"ttl"</span>, 
  * </code><code>  <span class="hljs-string">"unique"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code></pre>
  */
  def post(collectionName: String, body: PostAPIIndexTtl): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/index#ttl", append = true) 
    .params("collection-name" -> collectionName.toString)
    .restful[PostAPIIndexTtl, Json](body)
}