package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIReplicationLoggerFollow(client: HttpClient) {
  /**
  * Returns data from the server's replication log. This method can be called
  * by replication clients after an initial synchronization of data. The method
  * will return all "recent" log entries from the logger server, and the clients
  * can replay and apply these entries locally so they get to the same data
  * state as the logger server.
  * 
  * Clients can call this method repeatedly to incrementally fetch all changes
  * from the logger server. In this case, they should provide the *from* value so
  * they will only get returned the log events since their last fetch.
  * 
  * When the *from* query parameter is not used, the logger server will return log
  * entries starting at the beginning of its replication log. When the *from*
  * parameter is used, the logger server will only return log entries which have
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
  * - *cid*: id of the collection the event was for
  * 
  * - *tid*: id of the transaction the event was contained in
  * 
  * - *key*: document key
  * 
  * - *rev*: document revision id
  * 
  * - *data*: the original document data
  * 
  * A more detailed description of the individual replication event types and their
  * data structures can be found in [Operation Types](./WALAccess.md/#operation-types).
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
  * - *x-arango-replication-lasttick*: the last tick value the logger server has
  *   logged (not necessarily included in the result). By comparing the the last
  *   tick and last included tick values, clients have an approximate indication of
  *   how many events there are still left to fetch.
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
  * <!-- Hints Start -->
  * 
  * **Warning:**  
  * This route should no longer be used.
  * It is considered as deprecated from version 3.4.0 on.
  * 
  * 
  * 
  * <!-- Hints End -->
  * 
  * 
  * **Example:**
  *  No log events available
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/replication/logger-follow?from=105046</span>
  * </code><code>
  * </code><code>HTTP/1.1 No Content
  * </code><code>content-type: application/x-arango-dump; charset=utf-8
  * </code><code>x-arango-replication-active: true
  * </code><code>x-arango-replication-checkmore: false
  * </code><code>x-arango-replication-frompresent: true
  * </code><code>x-arango-replication-lastincluded: 0
  * </code><code>x-arango-replication-lastscanned: 105046
  * </code><code>x-arango-replication-lasttick: 105046
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/replication/logger-follow?from=105046</span>
  * </code><code>
  * </code><code>HTTP/1.1 OK
  * </code><code>content-type: application/x-arango-dump; charset=utf-8
  * </code><code>x-arango-replication-active: true
  * </code><code>x-arango-replication-checkmore: false
  * </code><code>x-arango-replication-frompresent: true
  * </code><code>x-arango-replication-lastincluded: 105067
  * </code><code>x-arango-replication-lastscanned: 105067
  * </code><code>x-arango-replication-lasttick: 105067
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"tick"</span> : <span class="hljs-string">"105050"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2000</span>, 
  * </code><code>  <span class="hljs-string">"database"</span> : <span class="hljs-string">"1"</span>, 
  * </code><code>  <span class="hljs-string">"cid"</span> : <span class="hljs-string">"105049"</span>, 
  * </code><code>  <span class="hljs-string">"cname"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"data"</span> : { 
  * </code><code>    <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"cid"</span> : <span class="hljs-string">"105049"</span>, 
  * </code><code>    <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"deleted"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>    <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/105049"</span>, 
  * </code><code>    <span class="hljs-string">"id"</span> : <span class="hljs-string">"105049"</span>, 
  * </code><code>    <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>    <span class="hljs-string">"indexes"</span> : [ 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-string">"0"</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"primary"</span>, 
  * </code><code>        <span class="hljs-string">"fields"</span> : [ 
  * </code><code>          <span class="hljs-string">"_key"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"unique"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      } 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>    <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>    <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>    <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>    <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>      <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>      <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"name"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>    <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>    <span class="hljs-string">"planId"</span> : <span class="hljs-string">"105049"</span>, 
  * </code><code>    <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">1</span>, 
  * </code><code>    <span class="hljs-string">"shardKeys"</span> : [ 
  * </code><code>      <span class="hljs-string">"_key"</span> 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"shards"</span> : { 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>    <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>    <span class="hljs-string">"version"</span> : <span class="hljs-number">7</span>, 
  * </code><code>    <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span> 
  * </code><code>  } 
  * </code><code>}&#x21A9;
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"tick"</span> : <span class="hljs-string">"105053"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2300</span>, 
  * </code><code>  <span class="hljs-string">"tid"</span> : <span class="hljs-string">"0"</span>, 
  * </code><code>  <span class="hljs-string">"database"</span> : <span class="hljs-string">"1"</span>, 
  * </code><code>  <span class="hljs-string">"cid"</span> : <span class="hljs-string">"8"</span>, 
  * </code><code>  <span class="hljs-string">"cname"</span> : <span class="hljs-string">"_users"</span>, 
  * </code><code>  <span class="hljs-string">"data"</span> : { 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"58"</span>, 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"_users/58"</span>, 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1VSi--_"</span>, 
  * </code><code>    <span class="hljs-string">"user"</span> : <span class="hljs-string">"root"</span>, 
  * </code><code>    <span class="hljs-string">"source"</span> : <span class="hljs-string">"LOCAL"</span>, 
  * </code><code>    <span class="hljs-string">"authData"</span> : { 
  * </code><code>      <span class="hljs-string">"active"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"simple"</span> : { 
  * </code><code>        <span class="hljs-string">"hash"</span> : <span class="hljs-string">"ba63424cac2432f605d770a3a2ca1c066f164ee2e022b3f6fa1c41bfa2391f6c"</span>, 
  * </code><code>        <span class="hljs-string">"salt"</span> : <span class="hljs-string">"93971d8d"</span>, 
  * </code><code>        <span class="hljs-string">"method"</span> : <span class="hljs-string">"sha256"</span> 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"databases"</span> : { 
  * </code><code>      <span class="hljs-string">"_system"</span> : { 
  * </code><code>        <span class="hljs-string">"permissions"</span> : { 
  * </code><code>          <span class="hljs-string">"read"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"write"</span> : <span class="hljs-literal">true</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"collections"</span> : { 
  * </code><code>          <span class="hljs-string">"demo"</span> : { 
  * </code><code>            <span class="hljs-string">"permissions"</span> : { 
  * </code><code>              <span class="hljs-string">"read"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>              <span class="hljs-string">"write"</span> : <span class="hljs-literal">true</span> 
  * </code><code>            } 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"animals"</span> : { 
  * </code><code>            <span class="hljs-string">"permissions"</span> : { 
  * </code><code>              <span class="hljs-string">"read"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>              <span class="hljs-string">"write"</span> : <span class="hljs-literal">true</span> 
  * </code><code>            } 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"products"</span> : { 
  * </code><code>            <span class="hljs-string">"permissions"</span> : { 
  * </code><code>              <span class="hljs-string">"read"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>              <span class="hljs-string">"write"</span> : <span class="hljs-literal">true</span> 
  * </code><code>            } 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"*"</span> : { 
  * </code><code>            <span class="hljs-string">"permissions"</span> : { 
  * </code><code>              <span class="hljs-string">"read"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>              <span class="hljs-string">"write"</span> : <span class="hljs-literal">true</span> 
  * </code><code>            } 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"products1"</span> : { 
  * </code><code>            <span class="hljs-string">"permissions"</span> : { 
  * </code><code>              <span class="hljs-string">"read"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>              <span class="hljs-string">"write"</span> : <span class="hljs-literal">true</span> 
  * </code><code>            } 
  * </code><code>          } 
  * </code><code>        } 
  * </code><code>      }, 
  * </code><code>      <span class="hljs-string">"*"</span> : { 
  * </code><code>        <span class="hljs-string">"permissions"</span> : { 
  * </code><code>          <span class="hljs-string">"read"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"write"</span> : <span class="hljs-literal">true</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"collections"</span> : { 
  * </code><code>          <span class="hljs-string">"*"</span> : { 
  * </code><code>            <span class="hljs-string">"permissions"</span> : { 
  * </code><code>              <span class="hljs-string">"read"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>              <span class="hljs-string">"write"</span> : <span class="hljs-literal">true</span> 
  * </code><code>            } 
  * </code><code>          } 
  * </code><code>        } 
  * </code><code>      } 
  * </code><code>    } 
  * </code><code>  } 
  * </code><code>}&#x21A9;
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"tick"</span> : <span class="hljs-string">"105057"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2300</span>, 
  * </code><code>  <span class="hljs-string">"tid"</span> : <span class="hljs-string">"0"</span>, 
  * </code><code>  <span class="hljs-string">"database"</span> : <span class="hljs-string">"1"</span>, 
  * </code><code>  <span class="hljs-string">"cid"</span> : <span class="hljs-string">"105049"</span>, 
  * </code><code>  <span class="hljs-string">"cname"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"data"</span> : { 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"p1"</span>, 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"_unknown/p1"</span>, 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1VSm--_"</span>, 
  * </code><code>    <span class="hljs-string">"name"</span> : <span class="hljs-string">"flux compensator"</span> 
  * </code><code>  } 
  * </code><code>}&#x21A9;
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"tick"</span> : <span class="hljs-string">"105059"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2300</span>, 
  * </code><code>  <span class="hljs-string">"tid"</span> : <span class="hljs-string">"0"</span>, 
  * </code><code>  <span class="hljs-string">"database"</span> : <span class="hljs-string">"1"</span>, 
  * </code><code>  <span class="hljs-string">"cid"</span> : <span class="hljs-string">"105049"</span>, 
  * </code><code>  <span class="hljs-string">"cname"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"data"</span> : { 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"p2"</span>, 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"_unknown/p2"</span>, 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1VSm--B"</span>, 
  * </code><code>    <span class="hljs-string">"name"</span> : <span class="hljs-string">"hybrid hovercraft"</span>, 
  * </code><code>    <span class="hljs-string">"hp"</span> : <span class="hljs-number">5100</span> 
  * </code><code>  } 
  * </code><code>}&#x21A9;
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"tick"</span> : <span class="hljs-string">"105061"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2302</span>, 
  * </code><code>  <span class="hljs-string">"tid"</span> : <span class="hljs-string">"0"</span>, 
  * </code><code>  <span class="hljs-string">"database"</span> : <span class="hljs-string">"1"</span>, 
  * </code><code>  <span class="hljs-string">"cid"</span> : <span class="hljs-string">"105049"</span>, 
  * </code><code>  <span class="hljs-string">"cname"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"data"</span> : { 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"p1"</span>, 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1VSm--D"</span> 
  * </code><code>  } 
  * </code><code>}&#x21A9;
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"tick"</span> : <span class="hljs-string">"105063"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2300</span>, 
  * </code><code>  <span class="hljs-string">"tid"</span> : <span class="hljs-string">"0"</span>, 
  * </code><code>  <span class="hljs-string">"database"</span> : <span class="hljs-string">"1"</span>, 
  * </code><code>  <span class="hljs-string">"cid"</span> : <span class="hljs-string">"105049"</span>, 
  * </code><code>  <span class="hljs-string">"cname"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"data"</span> : { 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"p2"</span>, 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"_unknown/p2"</span>, 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1VSq--_"</span>, 
  * </code><code>    <span class="hljs-string">"name"</span> : <span class="hljs-string">"broken hovercraft"</span>, 
  * </code><code>    <span class="hljs-string">"hp"</span> : <span class="hljs-number">5100</span> 
  * </code><code>  } 
  * </code><code>}&#x21A9;
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"tick"</span> : <span class="hljs-string">"105064"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2001</span>, 
  * </code><code>  <span class="hljs-string">"database"</span> : <span class="hljs-string">"1"</span>, 
  * </code><code>  <span class="hljs-string">"cid"</span> : <span class="hljs-string">"105049"</span>, 
  * </code><code>  <span class="hljs-string">"cname"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"data"</span> : { 
  * </code><code>    <span class="hljs-string">"id"</span> : <span class="hljs-string">"105049"</span>, 
  * </code><code>    <span class="hljs-string">"name"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>    <span class="hljs-string">"cuid"</span> : <span class="hljs-string">"h8B2B671BCFD0/105049"</span> 
  * </code><code>  } 
  * </code><code>}&#x21A9;
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"tick"</span> : <span class="hljs-string">"105067"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2300</span>, 
  * </code><code>  <span class="hljs-string">"tid"</span> : <span class="hljs-string">"0"</span>, 
  * </code><code>  <span class="hljs-string">"database"</span> : <span class="hljs-string">"1"</span>, 
  * </code><code>  <span class="hljs-string">"cid"</span> : <span class="hljs-string">"8"</span>, 
  * </code><code>  <span class="hljs-string">"cname"</span> : <span class="hljs-string">"_users"</span>, 
  * </code><code>  <span class="hljs-string">"data"</span> : { 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"58"</span>, 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"_users/58"</span>, 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1VTC--_"</span>, 
  * </code><code>    <span class="hljs-string">"user"</span> : <span class="hljs-string">"root"</span>, 
  * </code><code>    <span class="hljs-string">"source"</span> : <span class="hljs-string">"LOCAL"</span>, 
  * </code><code>    <span class="hljs-string">"authData"</span> : { 
  * </code><code>      <span class="hljs-string">"active"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"simple"</span> : { 
  * </code><code>        <span class="hljs-string">"hash"</span> : <span class="hljs-string">"ba63424cac2432f605d770a3a2ca1c066f164ee2e022b3f6fa1c41bfa2391f6c"</span>, 
  * </code><code>        <span class="hljs-string">"salt"</span> : <span class="hljs-string">"93971d8d"</span>, 
  * </code><code>        <span class="hljs-string">"method"</span> : <span class="hljs-string">"sha256"</span> 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"databases"</span> : { 
  * </code><code>      <span class="hljs-string">"*"</span> : { 
  * </code><code>        <span class="hljs-string">"permissions"</span> : { 
  * </code><code>          <span class="hljs-string">"read"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"write"</span> : <span class="hljs-literal">true</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"collections"</span> : { 
  * </code><code>          <span class="hljs-string">"*"</span> : { 
  * </code><code>            <span class="hljs-string">"permissions"</span> : { 
  * </code><code>              <span class="hljs-string">"read"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>              <span class="hljs-string">"write"</span> : <span class="hljs-literal">true</span> 
  * </code><code>            } 
  * </code><code>          } 
  * </code><code>        } 
  * </code><code>      }, 
  * </code><code>      <span class="hljs-string">"_system"</span> : { 
  * </code><code>        <span class="hljs-string">"permissions"</span> : { 
  * </code><code>          <span class="hljs-string">"read"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"write"</span> : <span class="hljs-literal">true</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"collections"</span> : { 
  * </code><code>          <span class="hljs-string">"products1"</span> : { 
  * </code><code>            <span class="hljs-string">"permissions"</span> : { 
  * </code><code>              <span class="hljs-string">"read"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>              <span class="hljs-string">"write"</span> : <span class="hljs-literal">true</span> 
  * </code><code>            } 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"*"</span> : { 
  * </code><code>            <span class="hljs-string">"permissions"</span> : { 
  * </code><code>              <span class="hljs-string">"read"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>              <span class="hljs-string">"write"</span> : <span class="hljs-literal">true</span> 
  * </code><code>            } 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"demo"</span> : { 
  * </code><code>            <span class="hljs-string">"permissions"</span> : { 
  * </code><code>              <span class="hljs-string">"read"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>              <span class="hljs-string">"write"</span> : <span class="hljs-literal">true</span> 
  * </code><code>            } 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"animals"</span> : { 
  * </code><code>            <span class="hljs-string">"permissions"</span> : { 
  * </code><code>              <span class="hljs-string">"read"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>              <span class="hljs-string">"write"</span> : <span class="hljs-literal">true</span> 
  * </code><code>            } 
  * </code><code>          } 
  * </code><code>        } 
  * </code><code>      } 
  * </code><code>    } 
  * </code><code>  } 
  * </code><code>}&#x21A9;
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  More events than would fit into the response
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/replication/logger-follow?from=105025&amp;chunkSize=400</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/x-arango-dump; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-arango-replication-active: <span class="hljs-literal">true</span>
  * </code><code>x-arango-replication-checkmore: <span class="hljs-literal">true</span>
  * </code><code>x-arango-replication-frompresent: <span class="hljs-literal">true</span>
  * </code><code>x-arango-replication-lastincluded: <span class="hljs-number">105029</span>
  * </code><code>x-arango-replication-lastscanned: <span class="hljs-number">105029</span>
  * </code><code>x-arango-replication-lasttick: <span class="hljs-number">105046</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"tick"</span> : <span class="hljs-string">"105029"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2000</span>, 
  * </code><code>  <span class="hljs-string">"database"</span> : <span class="hljs-string">"1"</span>, 
  * </code><code>  <span class="hljs-string">"cid"</span> : <span class="hljs-string">"105028"</span>, 
  * </code><code>  <span class="hljs-string">"cname"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"data"</span> : { 
  * </code><code>    <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"cid"</span> : <span class="hljs-string">"105028"</span>, 
  * </code><code>    <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"deleted"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>    <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/105028"</span>, 
  * </code><code>    <span class="hljs-string">"id"</span> : <span class="hljs-string">"105028"</span>, 
  * </code><code>    <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>    <span class="hljs-string">"indexes"</span> : [ 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-string">"0"</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"primary"</span>, 
  * </code><code>        <span class="hljs-string">"fields"</span> : [ 
  * </code><code>          <span class="hljs-string">"_key"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"unique"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      } 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>    <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>    <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>    <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>    <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>      <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>      <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"name"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>    <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>    <span class="hljs-string">"planId"</span> : <span class="hljs-string">"105028"</span>, 
  * </code><code>    <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">1</span>, 
  * </code><code>    <span class="hljs-string">"shardKeys"</span> : [ 
  * </code><code>      <span class="hljs-string">"_key"</span> 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"shards"</span> : { 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>    <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>    <span class="hljs-string">"version"</span> : <span class="hljs-number">7</span>, 
  * </code><code>    <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def get(from: Option[Double] = None, to: Option[Double] = None, chunkSize: Option[Double] = None, includeSystem: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_api/replication/logger-follow".withArguments(Map()))
    .param[Option[Double]]("from", from, None)
    .param[Option[Double]]("to", to, None)
    .param[Option[Double]]("chunkSize", chunkSize, None)
    .param[Option[Boolean]]("includeSystem", includeSystem, None)
    .call[ArangoResponse]
}