package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIWalTail(client: HttpClient) {
  /**
  * Returns data from the server's write-ahead log (also named replication log). This method can be called
  * by replication clients after an initial synchronization of data. The method
  * will return all "recent" logged operations from the server. Clients
  * can replay and apply these operations locally so they get to the same data
  * state as the server.
  * 
  * Clients can call this method repeatedly to incrementally fetch all changes
  * from the server. In this case, they should provide the *from* value so
  * they will only get returned the log events since their last fetch.
  * 
  * When the *from* query parameter is not used, the server will return log
  * entries starting at the beginning of its replication log. When the *from*
  * parameter is used, the server will only return log entries which have
  * higher tick values than the specified *from* value (note: the log entry with a
  * tick value equal to *from* will be excluded). Use the *from* value when
  * incrementally fetching log data.
  * 
  * The *to* query parameter can be used to optionally restrict the upper bound of
  * the result to a certain tick value. If used, the result will contain only log events
  * with tick values up to (including) *to*. In incremental fetching, there is no
  * need to use the *to* parameter. It only makes sense in special situations,
  * when only parts of the change log are required.
  * 
  * The *chunkSize* query parameter can be used to control the size of the result.
  * It must be specified in bytes. The *chunkSize* value will only be honored
  * approximately. Otherwise a too low *chunkSize* value could cause the server
  * to not be able to put just one log entry into the result and return it.
  * Therefore, the *chunkSize* value will only be consulted after a log entry has
  * been written into the result. If the result size is then bigger than
  * *chunkSize*, the server will respond with as many log entries as there are
  * in the response already. If the result size is still smaller than *chunkSize*,
  * the server will try to return more data if there's more data left to return.
  * 
  * If *chunkSize* is not specified, some server-side default value will be used.
  * 
  * The *Content-Type* of the result is *application/x-arango-dump*. This is an
  * easy-to-process format, with all log events going onto separate lines in the
  * response body. Each log event itself is a JSON object, with at least the
  * following attributes:
  * 
  * - *tick*: the log event tick value
  * 
  * - *type*: the log event type
  * 
  * Individual log events will also have additional attributes, depending on the
  * event type. A few common attributes which are used for multiple events types
  * are:
  * 
  * - *cuid*: globally unique id of the view or collection the event was for
  * 
  * - *db*: the database name the event was for
  * 
  * - *tid*: id of the transaction the event was contained in
  * 
  * - *data*: the original document data
  * 
  * A more detailed description of the individual replication event types and their
  * data structures can be found in [Operation Types](#operation-types).
  * 
  * The response will also contain the following HTTP headers:
  * 
  * - *x-arango-replication-active*: whether or not the logger is active. Clients
  *   can use this flag as an indication for their polling frequency. If the
  *   logger is not active and there are no more replication events available, it
  *   might be sensible for a client to abort, or to go to sleep for a long time
  *   and try again later to check whether the logger has been activated.
  * 
  * - *x-arango-replication-lastincluded*: the tick value of the last included
  *   value in the result. In incremental log fetching, this value can be used
  *   as the *from* value for the following request. **Note** that if the result is
  *   empty, the value will be *0*. This value should not be used as *from* value
  *   by clients in the next request (otherwise the server would return the log
  *   events from the start of the log again).
  * 
  * - *x-arango-replication-lastscanned*: the last tick the server scanned while
  *   computing the operation log. This might include operations the server did not
  *   returned to you due to various reasons (i.e. the value was filtered or skipped).
  *   You may use this value in the *lastScanned* header to allow the rocksdb engine
  *   to break up requests over multiple responses.
  * 
  * - *x-arango-replication-lasttick*: the last tick value the server has
  *   logged in its write ahead log (not necessarily included in the result). By comparing the the last
  *   tick and last included tick values, clients have an approximate indication of
  *   how many events there are still left to fetch.
  * 
  * - *x-arango-replication-frompresent*: is set to _true_ if server returned
  *   all tick values starting from the specified tick in the _from_ parameter.
  *   Should this be set to false the server did not have these operations anymore
  *   and the client might have missed operations.
  * 
  * - *x-arango-replication-checkmore*: whether or not there already exists more
  *   log data which the client could fetch immediately. If there is more log data
  *   available, the client could call *logger-follow* again with an adjusted *from*
  *   value to fetch remaining log entries until there are no more.
  * 
  *   If there isn't any more log data to fetch, the client might decide to go
  *   to sleep for a while before calling the logger again.
  * 
  * **Note**: this method is not supported on a coordinator in a cluster.
  * 
  * 
  * 
  * 
  * **Example:**
  *  No log events available
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/wal/tail?from=107382</span>
  * </code><code>
  * </code><code>HTTP/1.1 No Content
  * </code><code>content-type: application/x-arango-dump; charset=utf-8
  * </code><code>x-arango-replication-checkmore: false
  * </code><code>x-arango-replication-frompresent: true
  * </code><code>x-arango-replication-lastincluded: 0
  * </code><code>x-arango-replication-lastscanned: 107382
  * </code><code>x-arango-replication-lasttick: 107382
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  A few log events *(One JSON document per line)*
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/wal/tail?from=107382</span>
  * </code><code>
  * </code><code>HTTP/1.1 OK
  * </code><code>content-type: application/x-arango-dump; charset=utf-8
  * </code><code>x-arango-replication-checkmore: true
  * </code><code>x-arango-replication-frompresent: true
  * </code><code>x-arango-replication-lastincluded: 107400
  * </code><code>x-arango-replication-lastscanned: 107403
  * </code><code>x-arango-replication-lasttick: 107403
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"tick"</span> : <span class="hljs-string">"107400"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2001</span>, 
  * </code><code>  <span class="hljs-string">"db"</span> : <span class="hljs-string">"_system"</span>, 
  * </code><code>  <span class="hljs-string">"cuid"</span> : <span class="hljs-string">"h8B2B671BCFD0/107385"</span> 
  * </code><code>}&#x21A9;
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  More events than would fit into the response
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/wal/tail?from=107352&amp;chunkSize=400</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/x-arango-dump; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-arango-replication-checkmore: <span class="hljs-literal">true</span>
  * </code><code>x-arango-replication-frompresent: <span class="hljs-literal">true</span>
  * </code><code>x-arango-replication-lastincluded: <span class="hljs-number">107370</span>
  * </code><code>x-arango-replication-lastscanned: <span class="hljs-number">107382</span>
  * </code><code>x-arango-replication-lasttick: <span class="hljs-number">107382</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"tick"</span> : <span class="hljs-string">"107370"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2001</span>, 
  * </code><code>  <span class="hljs-string">"db"</span> : <span class="hljs-string">"_system"</span>, 
  * </code><code>  <span class="hljs-string">"cuid"</span> : <span class="hljs-string">"h8B2B671BCFD0/107355"</span> 
  * </code><code>}
  * </code></pre>
  */
  def get(from: Option[Double] = None, to: Option[Double] = None, lastScanned: Option[Double] = None, global: Option[Boolean] = None, chunkSize: Option[Double] = None, serverId: Option[Double] = None, barrierId: Option[Double] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_api/wal/tail".withArguments(Map()))
    .param[Option[Double]]("from", from, None)
    .param[Option[Double]]("to", to, None)
    .param[Option[Double]]("lastScanned", lastScanned, None)
    .param[Option[Boolean]]("global", global, None)
    .param[Option[Double]]("chunkSize", chunkSize, None)
    .param[Option[Double]]("serverId", serverId, None)
    .param[Option[Double]]("barrierId", barrierId, None)
    .call[ArangoResponse]
}