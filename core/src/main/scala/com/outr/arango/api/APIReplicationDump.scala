package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIReplicationDump(client: HttpClient) {
  /**
  * Returns the data from the collection for the requested range.
  * 
  * When the *from* query parameter is not used, collection events are returned from
  * the beginning. When the *from* parameter is used, the result will only contain
  * collection entries which have higher tick values than the specified *from* value
  * (note: the log entry with a tick value equal to *from* will be excluded).
  * 
  * The *to* query parameter can be used to optionally restrict the upper bound of
  * the result to a certain tick value. If used, the result will only contain
  * collection entries with tick values up to (including) *to*.
  * 
  * The *chunkSize* query parameter can be used to control the size of the result.
  * It must be specified in bytes. The *chunkSize* value will only be honored
  * approximately. Otherwise a too low *chunkSize* value could cause the server
  * to not be able to put just one entry into the result and return it.
  * Therefore, the *chunkSize* value will only be consulted after an entry has
  * been written into the result. If the result size is then bigger than
  * *chunkSize*, the server will respond with as many entries as there are
  * in the response already. If the result size is still smaller than *chunkSize*,
  * the server will try to return more data if there's more data left to return.
  * 
  * If *chunkSize* is not specified, some server-side default value will be used.
  * 
  * The *Content-Type* of the result is *application/x-arango-dump*. This is an
  * easy-to-process format, with all entries going onto separate lines in the
  * response body.
  * 
  * Each line itself is a JSON object, with at least the following attributes:
  * 
  * - *tick*: the operation's tick attribute
  * 
  * - *key*: the key of the document/edge or the key used in the deletion operation
  * 
  * - *rev*: the revision id of the document/edge or the deletion operation
  * 
  * - *data*: the actual document/edge data for types 2300 and 2301. The full
  *   document/edge data will be returned even for updates.
  * 
  * - *type*: the type of entry. Possible values for *type* are:
  * 
  *   - 2300: document insertion/update
  * 
  *   - 2301: edge insertion/update
  * 
  *   - 2302: document/edge deletion
  * 
  * **Note**: there will be no distinction between inserts and updates when calling this method.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Empty collection:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/replication/dump?collection=testCollection</span>
  * </code><code>
  * </code><code>HTTP/1.1 No Content
  * </code><code>content-type: application/x-arango-dump; charset=utf-8
  * </code><code>x-arango-replication-checkmore: false
  * </code><code>x-arango-replication-lastincluded: 0
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Non-empty collection *(One JSON document per line)*:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/replication/dump?collection=testCollection</span>
  * </code><code>
  * </code><code>HTTP/1.1 OK
  * </code><code>content-type: application/x-arango-dump; charset=utf-8
  * </code><code>x-arango-replication-checkmore: false
  * </code><code>x-arango-replication-lastincluded: 104964
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"tick"</span> : <span class="hljs-string">"104958"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2300</span>, 
  * </code><code>  <span class="hljs-string">"data"</span> : { 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"123456"</span>, 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"testCollection/123456"</span>, 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1QmK--_"</span>, 
  * </code><code>    <span class="hljs-string">"b"</span> : <span class="hljs-number">1</span>, 
  * </code><code>    <span class="hljs-string">"c"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>    <span class="hljs-string">"d"</span> : <span class="hljs-string">"additional value"</span> 
  * </code><code>  } 
  * </code><code>}&#x21A9;
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"tick"</span> : <span class="hljs-string">"104962"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2302</span>, 
  * </code><code>  <span class="hljs-string">"data"</span> : { 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"foobar"</span>, 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1QmK--D"</span> 
  * </code><code>  } 
  * </code><code>}&#x21A9;
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"tick"</span> : <span class="hljs-string">"104964"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2302</span>, 
  * </code><code>  <span class="hljs-string">"data"</span> : { 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"abcdef"</span>, 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1QmO--_"</span> 
  * </code><code>  } 
  * </code><code>}&#x21A9;
  * </code></pre>
  */
  def get(collection: String, chunkSize: Option[Double] = None, batchId: Double, from: Option[Double] = None, to: Option[Double] = None, includeSystem: Option[Boolean] = None, ticks: Option[Boolean] = None, flush: Option[Boolean] = None): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/replication/dump", append = true) 
    .params("collection" -> collection.toString)
    .param[Option[Double]]("chunkSize", chunkSize, None)
    .params("batchId" -> batchId.toString)
    .param[Option[Double]]("from", from, None)
    .param[Option[Double]]("to", to, None)
    .param[Option[Boolean]]("includeSystem", includeSystem, None)
    .param[Option[Boolean]]("ticks", ticks, None)
    .param[Option[Boolean]]("flush", flush, None)
    .call[Json]
}