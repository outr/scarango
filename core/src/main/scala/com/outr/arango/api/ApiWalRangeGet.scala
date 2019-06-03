package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiWalRangeGet(client: HttpClient) {
  /**
  * Returns the currently available ranges of tick values for all WAL files.
  * The tick values can be used to determine if certain
  * data (identified by tick value) are still available for replication.
  * 
  * The body of the response contains a JSON object. 
  * * *tickMin*: minimum tick available
  * * *tickMax: maximum tick available
  * * *time*: the server time as string in format "YYYY-MM-DDTHH:MM:SSZ"
  * * *server*: An object with fields *version* and *serverId*
  * 
  * 
  * 
  * 
  * **Example:**
  *  Returns the available tick ranges.
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/wal/range</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"time"</span> : <span class="hljs-string">"2019-02-20T10:33:16Z"</span>, 
  * </code><code>  <span class="hljs-string">"tickMin"</span> : <span class="hljs-string">"5"</span>, 
  * </code><code>  <span class="hljs-string">"tickMax"</span> : <span class="hljs-string">"107403"</span>, 
  * </code><code>  <span class="hljs-string">"server"</span> : { 
  * </code><code>    <span class="hljs-string">"version"</span> : <span class="hljs-string">"3.5.0-devel"</span>, 
  * </code><code>    <span class="hljs-string">"serverId"</span> : <span class="hljs-string">"153018529730512"</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def get(): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .call[ArangoResponse]
}