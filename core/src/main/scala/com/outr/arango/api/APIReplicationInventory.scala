package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIReplicationInventory(client: HttpClient) {
  /**
  * Returns the array of collections and indexes available on the server. This
  * array can be used by replication clients to initiate an initial sync with the
  * server.
  * 
  * The response will contain a JSON object with the *collection* and *state* and
  * *tick* attributes.
  * 
  * *collections* is an array of collections with the following sub-attributes:
  * 
  * - *parameters*: the collection properties
  * 
  * - *indexes*: an array of the indexes of a the collection. Primary indexes and edge indexes
  *    are not included in this array.
  * 
  * The *state* attribute contains the current state of the replication logger. It
  * contains the following sub-attributes:
  * 
  * - *running*: whether or not the replication logger is currently active. Note:
  *   since ArangoDB 2.2, the value will always be *true*
  * 
  * - *lastLogTick*: the value of the last tick the replication logger has written
  * 
  * - *time*: the current time on the server
  * 
  * Replication clients should note the *lastLogTick* value returned. They can then
  * fetch collections' data using the dump method up to the value of lastLogTick, and
  * query the continuous replication log for log events after this tick value.
  * 
  * To create a full copy of the collections on the server, a replication client
  * can execute these steps:
  * 
  * - call the {@literal *}/inventory* API method. This returns the *lastLogTick* value and the
  *   array of collections and indexes from the server.
  * 
  * - for each collection returned by {@literal *}/inventory*, create the collection locally and
  *   call {@literal *}/dump* to stream the collection data to the client, up to the value of
  *   *lastLogTick*.
  *   After that, the client can create the indexes on the collections as they were
  *   reported by {@literal *}/inventory*.
  * 
  * If the clients wants to continuously stream replication log events from the logger
  * server, the following additional steps need to be carried out:
  * 
  * - the client should call {@literal *}/logger-follow* initially to fetch the first batch of
  *   replication events that were logged after the client's call to {@literal *}/inventory*.
  * 
  *   The call to {@literal *}/logger-follow* should use a *from* parameter with the value of the
  *   *lastLogTick* as reported by {@literal *}/inventory*. The call to {@literal *}/logger-follow* will return the
  *   *x-arango-replication-lastincluded* which will contain the last tick value included
  *   in the response.
  * 
  * - the client can then continuously call {@literal *}/logger-follow* to incrementally fetch new
  *   replication events that occurred after the last transfer.
  * 
  *   Calls should use a *from* parameter with the value of the *x-arango-replication-lastincluded*
  *   header of the previous response. If there are no more replication events, the
  *   response will be empty and clients can go to sleep for a while and try again
  *   later.
  * 
  * **Note**: on a coordinator, this request must have the query parameter
  * *DBserver* which must be an ID of a DBserver.
  * The very same request is forwarded synchronously to that DBserver.
  * It is an error if this attribute is not bound in the coordinator case.
  * 
  * **Note:**: Using the `global` parameter the top-level object contains a key `databases`
  * under which each key represents a datbase name, and the value conforms to the above describtion.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/replication/inventory</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collections"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"indexes"</span> : [ ], 
  * </code><code>      <span class="hljs-string">"parameters"</span> : { 
  * </code><code>        <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"cid"</span> : <span class="hljs-string">"32"</span>, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"deleted"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_appbundles"</span>, 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-string">"32"</span>, 
  * </code><code>        <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>        <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">1048576</span>, 
  * </code><code>        <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>          <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>          <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"_appbundles"</span>, 
  * </code><code>        <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"planId"</span> : <span class="hljs-string">"32"</span>, 
  * </code><code>        <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"shardKeys"</span> : [ 
  * </code><code>          <span class="hljs-string">"_key"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"shards"</span> : { 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"version"</span> : <span class="hljs-number">7</span>, 
  * </code><code>        <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"indexes"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-string">"30"</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"hash"</span>, 
  * </code><code>          <span class="hljs-string">"fields"</span> : [ 
  * </code><code>            <span class="hljs-string">"mount"</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"unique"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"deduplicate"</span> : <span class="hljs-literal">true</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"parameters"</span> : { 
  * </code><code>        <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"cid"</span> : <span class="hljs-string">"27"</span>, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"deleted"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_apps"</span>, 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-string">"27"</span>, 
  * </code><code>        <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>        <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">1048576</span>, 
  * </code><code>        <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>          <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>          <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">41</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"_apps"</span>, 
  * </code><code>        <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"planId"</span> : <span class="hljs-string">"27"</span>, 
  * </code><code>        <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"shardKeys"</span> : [ 
  * </code><code>          <span class="hljs-string">"_key"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"shards"</span> : { 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"version"</span> : <span class="hljs-number">7</span>, 
  * </code><code>        <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"indexes"</span> : [ ], 
  * </code><code>      <span class="hljs-string">"parameters"</span> : { 
  * </code><code>        <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"cid"</span> : <span class="hljs-string">"13"</span>, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"deleted"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_aqlfunctions"</span>, 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-string">"13"</span>, 
  * </code><code>        <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>        <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">1048576</span>, 
  * </code><code>        <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>          <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>          <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"_aqlfunctions"</span>, 
  * </code><code>        <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"planId"</span> : <span class="hljs-string">"13"</span>, 
  * </code><code>        <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"shardKeys"</span> : [ 
  * </code><code>          <span class="hljs-string">"_key"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"shards"</span> : { 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"version"</span> : <span class="hljs-number">7</span>, 
  * </code><code>        <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"indexes"</span> : [ ], 
  * </code><code>      <span class="hljs-string">"parameters"</span> : { 
  * </code><code>        <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"cid"</span> : <span class="hljs-string">"6"</span>, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"deleted"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_graphs"</span>, 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-string">"6"</span>, 
  * </code><code>        <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>        <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">1048576</span>, 
  * </code><code>        <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>          <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>          <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"_graphs"</span>, 
  * </code><code>        <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"planId"</span> : <span class="hljs-string">"6"</span>, 
  * </code><code>        <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"shardKeys"</span> : [ 
  * </code><code>          <span class="hljs-string">"_key"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"shards"</span> : { 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"version"</span> : <span class="hljs-number">7</span>, 
  * </code><code>        <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"indexes"</span> : [ ], 
  * </code><code>      <span class="hljs-string">"parameters"</span> : { 
  * </code><code>        <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"cid"</span> : <span class="hljs-string">"2"</span>, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"deleted"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_iresearch_analyzers"</span>, 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-string">"2"</span>, 
  * </code><code>        <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>        <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>        <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>          <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>          <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"_iresearch_analyzers"</span>, 
  * </code><code>        <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"planId"</span> : <span class="hljs-string">"2"</span>, 
  * </code><code>        <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"shardKeys"</span> : [ 
  * </code><code>          <span class="hljs-string">"_key"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"shards"</span> : { 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"version"</span> : <span class="hljs-number">7</span>, 
  * </code><code>        <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"indexes"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-string">"11"</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"hash"</span>, 
  * </code><code>          <span class="hljs-string">"fields"</span> : [ 
  * </code><code>            <span class="hljs-string">"user"</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"unique"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"deduplicate"</span> : <span class="hljs-literal">true</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"parameters"</span> : { 
  * </code><code>        <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"cid"</span> : <span class="hljs-string">"8"</span>, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"deleted"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_users"</span>, 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-string">"8"</span>, 
  * </code><code>        <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>        <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">1048576</span>, 
  * </code><code>        <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>          <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>          <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">104933</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"_users"</span>, 
  * </code><code>        <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"planId"</span> : <span class="hljs-string">"8"</span>, 
  * </code><code>        <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"shardKeys"</span> : [ 
  * </code><code>          <span class="hljs-string">"_key"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"shards"</span> : { 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"version"</span> : <span class="hljs-number">7</span>, 
  * </code><code>        <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"indexes"</span> : [ ], 
  * </code><code>      <span class="hljs-string">"parameters"</span> : { 
  * </code><code>        <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"cid"</span> : <span class="hljs-string">"96"</span>, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"deleted"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/96"</span>, 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-string">"96"</span>, 
  * </code><code>        <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>        <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>        <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>          <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>          <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"animals"</span>, 
  * </code><code>        <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"planId"</span> : <span class="hljs-string">"96"</span>, 
  * </code><code>        <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"shardKeys"</span> : [ 
  * </code><code>          <span class="hljs-string">"_key"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"shards"</span> : { 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"version"</span> : <span class="hljs-number">7</span>, 
  * </code><code>        <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"indexes"</span> : [ ], 
  * </code><code>      <span class="hljs-string">"parameters"</span> : { 
  * </code><code>        <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"cid"</span> : <span class="hljs-string">"87"</span>, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"deleted"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/87"</span>, 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-string">"87"</span>, 
  * </code><code>        <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>        <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>        <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>          <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>          <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"demo"</span>, 
  * </code><code>        <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"planId"</span> : <span class="hljs-string">"87"</span>, 
  * </code><code>        <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"shardKeys"</span> : [ 
  * </code><code>          <span class="hljs-string">"_key"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"shards"</span> : { 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"version"</span> : <span class="hljs-number">7</span>, 
  * </code><code>        <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      } 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"views"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/102"</span>, 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"102"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"demoView"</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"arangosearch"</span>, 
  * </code><code>      <span class="hljs-string">"cleanupIntervalStep"</span> : <span class="hljs-number">10</span>, 
  * </code><code>      <span class="hljs-string">"commitIntervalMsec"</span> : <span class="hljs-number">60000</span>, 
  * </code><code>      <span class="hljs-string">"consolidationIntervalMsec"</span> : <span class="hljs-number">60000</span>, 
  * </code><code>      <span class="hljs-string">"consolidationPolicy"</span> : { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"bytes_accum"</span>, 
  * </code><code>        <span class="hljs-string">"threshold"</span> : <span class="hljs-number">0.10000000149011612</span> 
  * </code><code>      }, 
  * </code><code>      <span class="hljs-string">"writebufferActive"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"writebufferIdle"</span> : <span class="hljs-number">64</span>, 
  * </code><code>      <span class="hljs-string">"writebufferSizeMax"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>      <span class="hljs-string">"links"</span> : { 
  * </code><code>      } 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"state"</span> : { 
  * </code><code>    <span class="hljs-string">"running"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"lastLogTick"</span> : <span class="hljs-string">"104983"</span>, 
  * </code><code>    <span class="hljs-string">"lastUncommittedLogTick"</span> : <span class="hljs-string">"104989"</span>, 
  * </code><code>    <span class="hljs-string">"totalEvents"</span> : <span class="hljs-number">35260</span>, 
  * </code><code>    <span class="hljs-string">"time"</span> : <span class="hljs-string">"2019-02-20T10:33:06Z"</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"tick"</span> : <span class="hljs-string">"104990"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  With some additional indexes:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/replication/inventory</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collections"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"indexes"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-string">"105000"</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"hash"</span>, 
  * </code><code>          <span class="hljs-string">"fields"</span> : [ 
  * </code><code>            <span class="hljs-string">"name"</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"unique"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"deduplicate"</span> : <span class="hljs-literal">true</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-string">"105003"</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"skiplist"</span>, 
  * </code><code>          <span class="hljs-string">"fields"</span> : [ 
  * </code><code>            <span class="hljs-string">"a"</span>, 
  * </code><code>            <span class="hljs-string">"b"</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"unique"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"deduplicate"</span> : <span class="hljs-literal">true</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"parameters"</span> : { 
  * </code><code>        <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"cid"</span> : <span class="hljs-string">"104993"</span>, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"deleted"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/104993"</span>, 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-string">"104993"</span>, 
  * </code><code>        <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>        <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>        <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>          <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>          <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"IndexedCollection1"</span>, 
  * </code><code>        <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"planId"</span> : <span class="hljs-string">"104993"</span>, 
  * </code><code>        <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"shardKeys"</span> : [ 
  * </code><code>          <span class="hljs-string">"_key"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"shards"</span> : { 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"version"</span> : <span class="hljs-number">7</span>, 
  * </code><code>        <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"indexes"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-string">"105012"</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"fulltext"</span>, 
  * </code><code>          <span class="hljs-string">"fields"</span> : [ 
  * </code><code>            <span class="hljs-string">"text"</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"unique"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"minLength"</span> : <span class="hljs-number">10</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-string">"105015"</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"skiplist"</span>, 
  * </code><code>          <span class="hljs-string">"fields"</span> : [ 
  * </code><code>            <span class="hljs-string">"a"</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"unique"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"deduplicate"</span> : <span class="hljs-literal">true</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"parameters"</span> : { 
  * </code><code>        <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"cid"</span> : <span class="hljs-string">"105005"</span>, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"deleted"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/105005"</span>, 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-string">"105005"</span>, 
  * </code><code>        <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>        <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>        <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>          <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>          <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"IndexedCollection2"</span>, 
  * </code><code>        <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"planId"</span> : <span class="hljs-string">"105005"</span>, 
  * </code><code>        <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"shardKeys"</span> : [ 
  * </code><code>          <span class="hljs-string">"_key"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"shards"</span> : { 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"version"</span> : <span class="hljs-number">7</span>, 
  * </code><code>        <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"indexes"</span> : [ ], 
  * </code><code>      <span class="hljs-string">"parameters"</span> : { 
  * </code><code>        <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"cid"</span> : <span class="hljs-string">"32"</span>, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"deleted"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_appbundles"</span>, 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-string">"32"</span>, 
  * </code><code>        <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>        <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">1048576</span>, 
  * </code><code>        <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>          <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>          <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"_appbundles"</span>, 
  * </code><code>        <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"planId"</span> : <span class="hljs-string">"32"</span>, 
  * </code><code>        <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"shardKeys"</span> : [ 
  * </code><code>          <span class="hljs-string">"_key"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"shards"</span> : { 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"version"</span> : <span class="hljs-number">7</span>, 
  * </code><code>        <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"indexes"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-string">"30"</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"hash"</span>, 
  * </code><code>          <span class="hljs-string">"fields"</span> : [ 
  * </code><code>            <span class="hljs-string">"mount"</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"unique"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"deduplicate"</span> : <span class="hljs-literal">true</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"parameters"</span> : { 
  * </code><code>        <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"cid"</span> : <span class="hljs-string">"27"</span>, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"deleted"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_apps"</span>, 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-string">"27"</span>, 
  * </code><code>        <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>        <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">1048576</span>, 
  * </code><code>        <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>          <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>          <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">41</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"_apps"</span>, 
  * </code><code>        <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"planId"</span> : <span class="hljs-string">"27"</span>, 
  * </code><code>        <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"shardKeys"</span> : [ 
  * </code><code>          <span class="hljs-string">"_key"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"shards"</span> : { 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"version"</span> : <span class="hljs-number">7</span>, 
  * </code><code>        <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"indexes"</span> : [ ], 
  * </code><code>      <span class="hljs-string">"parameters"</span> : { 
  * </code><code>        <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"cid"</span> : <span class="hljs-string">"13"</span>, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"deleted"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_aqlfunctions"</span>, 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-string">"13"</span>, 
  * </code><code>        <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>        <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">1048576</span>, 
  * </code><code>        <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>          <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>          <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"_aqlfunctions"</span>, 
  * </code><code>        <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"planId"</span> : <span class="hljs-string">"13"</span>, 
  * </code><code>        <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"shardKeys"</span> : [ 
  * </code><code>          <span class="hljs-string">"_key"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"shards"</span> : { 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"version"</span> : <span class="hljs-number">7</span>, 
  * </code><code>        <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"indexes"</span> : [ ], 
  * </code><code>      <span class="hljs-string">"parameters"</span> : { 
  * </code><code>        <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"cid"</span> : <span class="hljs-string">"6"</span>, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"deleted"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_graphs"</span>, 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-string">"6"</span>, 
  * </code><code>        <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>        <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">1048576</span>, 
  * </code><code>        <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>          <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>          <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"_graphs"</span>, 
  * </code><code>        <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"planId"</span> : <span class="hljs-string">"6"</span>, 
  * </code><code>        <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"shardKeys"</span> : [ 
  * </code><code>          <span class="hljs-string">"_key"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"shards"</span> : { 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"version"</span> : <span class="hljs-number">7</span>, 
  * </code><code>        <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"indexes"</span> : [ ], 
  * </code><code>      <span class="hljs-string">"parameters"</span> : { 
  * </code><code>        <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"cid"</span> : <span class="hljs-string">"2"</span>, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"deleted"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_iresearch_analyzers"</span>, 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-string">"2"</span>, 
  * </code><code>        <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>        <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>        <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>          <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>          <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"_iresearch_analyzers"</span>, 
  * </code><code>        <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"planId"</span> : <span class="hljs-string">"2"</span>, 
  * </code><code>        <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"shardKeys"</span> : [ 
  * </code><code>          <span class="hljs-string">"_key"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"shards"</span> : { 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"version"</span> : <span class="hljs-number">7</span>, 
  * </code><code>        <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"indexes"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-string">"11"</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"hash"</span>, 
  * </code><code>          <span class="hljs-string">"fields"</span> : [ 
  * </code><code>            <span class="hljs-string">"user"</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"unique"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"deduplicate"</span> : <span class="hljs-literal">true</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"parameters"</span> : { 
  * </code><code>        <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"cid"</span> : <span class="hljs-string">"8"</span>, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"deleted"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"_users"</span>, 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-string">"8"</span>, 
  * </code><code>        <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>        <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">1048576</span>, 
  * </code><code>        <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>          <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>          <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">104933</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"_users"</span>, 
  * </code><code>        <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"planId"</span> : <span class="hljs-string">"8"</span>, 
  * </code><code>        <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"shardKeys"</span> : [ 
  * </code><code>          <span class="hljs-string">"_key"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"shards"</span> : { 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"version"</span> : <span class="hljs-number">7</span>, 
  * </code><code>        <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"indexes"</span> : [ ], 
  * </code><code>      <span class="hljs-string">"parameters"</span> : { 
  * </code><code>        <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"cid"</span> : <span class="hljs-string">"96"</span>, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"deleted"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/96"</span>, 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-string">"96"</span>, 
  * </code><code>        <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>        <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>        <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>          <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>          <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"animals"</span>, 
  * </code><code>        <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"planId"</span> : <span class="hljs-string">"96"</span>, 
  * </code><code>        <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"shardKeys"</span> : [ 
  * </code><code>          <span class="hljs-string">"_key"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"shards"</span> : { 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"version"</span> : <span class="hljs-number">7</span>, 
  * </code><code>        <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      } 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"indexes"</span> : [ ], 
  * </code><code>      <span class="hljs-string">"parameters"</span> : { 
  * </code><code>        <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"cid"</span> : <span class="hljs-string">"87"</span>, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"deleted"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/87"</span>, 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-string">"87"</span>, 
  * </code><code>        <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>        <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>        <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>          <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>          <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"demo"</span>, 
  * </code><code>        <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"planId"</span> : <span class="hljs-string">"87"</span>, 
  * </code><code>        <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"shardKeys"</span> : [ 
  * </code><code>          <span class="hljs-string">"_key"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"shards"</span> : { 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"version"</span> : <span class="hljs-number">7</span>, 
  * </code><code>        <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      } 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"views"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/102"</span>, 
  * </code><code>      <span class="hljs-string">"id"</span> : <span class="hljs-string">"102"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"demoView"</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"arangosearch"</span>, 
  * </code><code>      <span class="hljs-string">"cleanupIntervalStep"</span> : <span class="hljs-number">10</span>, 
  * </code><code>      <span class="hljs-string">"commitIntervalMsec"</span> : <span class="hljs-number">60000</span>, 
  * </code><code>      <span class="hljs-string">"consolidationIntervalMsec"</span> : <span class="hljs-number">60000</span>, 
  * </code><code>      <span class="hljs-string">"consolidationPolicy"</span> : { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"bytes_accum"</span>, 
  * </code><code>        <span class="hljs-string">"threshold"</span> : <span class="hljs-number">0.10000000149011612</span> 
  * </code><code>      }, 
  * </code><code>      <span class="hljs-string">"writebufferActive"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"writebufferIdle"</span> : <span class="hljs-number">64</span>, 
  * </code><code>      <span class="hljs-string">"writebufferSizeMax"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>      <span class="hljs-string">"links"</span> : { 
  * </code><code>      } 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"state"</span> : { 
  * </code><code>    <span class="hljs-string">"running"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"lastLogTick"</span> : <span class="hljs-string">"104983"</span>, 
  * </code><code>    <span class="hljs-string">"lastUncommittedLogTick"</span> : <span class="hljs-string">"105016"</span>, 
  * </code><code>    <span class="hljs-string">"totalEvents"</span> : <span class="hljs-number">35268</span>, 
  * </code><code>    <span class="hljs-string">"time"</span> : <span class="hljs-string">"2019-02-20T10:33:06Z"</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"tick"</span> : <span class="hljs-string">"105016"</span> 
  * </code><code>}
  * </code></pre>
  */
  def get(includeSystem: Option[Boolean] = None, _global: Option[Boolean] = None, batchId: Double): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/replication/inventory", append = true) 
    .param[Option[Boolean]]("includeSystem", includeSystem, None)
    .param[Option[Boolean]]("global", _global, None)
    .params("batchId" -> batchId.toString)
    .call[Json]
}