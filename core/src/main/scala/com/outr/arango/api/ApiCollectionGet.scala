package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiCollectionGet(client: HttpClient) {
  /**
  * Returns an object with an attribute *collections* containing an
  * array of all collection descriptions. The same information is also
  * available in the *names* as an object with the collection names
  * as keys.
  * 
  * By providing the optional query parameter *excludeSystem* with a value of
  * *true*, all system collections will be excluded from the response.
  * 
  * 
  * <!-- Hints Start -->
  * 
  * **Warning:**  
  * Accessing collections by their numeric ID is deprecated from version 3.4.0 on.
  * You should reference them via their names instead.
  * 
  * 
  * 
  * <!-- Hints End -->
  * 
  * 
  * **Example:**
  *  Return information about all collections:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/collection</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"result"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"17"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"_queues"</span>, 
  * </code><code>      <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_queues"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"15"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"_frontend"</span>, 
  * </code><code>      <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_frontend"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"32"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"_appbundles"</span>, 
  * </code><code>      <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_appbundles"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"66"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"_statistics"</span>, 
  * </code><code>      <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_statistics"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"8"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"_users"</span>, 
  * </code><code>      <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_users"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"2"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"_iresearch_analyzers"</span>, 
  * </code><code>      <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_iresearch_analyzers"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"19"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"_jobs"</span>, 
  * </code><code>      <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_jobs"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"87"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"demo"</span>, 
  * </code><code>      <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>      <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/87"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"13"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"_aqlfunctions"</span>, 
  * </code><code>      <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_aqlfunctions"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"6"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"_graphs"</span>, 
  * </code><code>      <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_graphs"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"27"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"_apps"</span>, 
  * </code><code>      <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_apps"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"61"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"_statisticsRaw"</span>, 
  * </code><code>      <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_statisticsRaw"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"71"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"_statistics15"</span>, 
  * </code><code>      <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_statistics15"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"96"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"animals"</span>, 
  * </code><code>      <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>      <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/96"</span> 
  * </code><code>    } 
  * </code><code>  ] 
  * </code><code>}
  * </code></pre>
  */
  def get(excludeSystem: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .param[Option[Boolean]]("excludeSystem", excludeSystem, None)
    .call[ArangoResponse]
}