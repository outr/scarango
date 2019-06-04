package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIViewarangosearch(client: HttpClient) {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **type**: The type of the view. must be equal to one of the supported ArangoDB view
  *    types.
  *   - **name**: The name of the view.
  *   - **properties**:
  *     - **commitIntervalMsec**: Wait at least this many milliseconds between committing view data store
  *     changes and making documents visible to queries (default: 1000, to disable
  *     use: 0).
  *     For the case where there are a lot of inserts/updates, a lower value, until
  *     commit, will cause the index not to account for them and memory usage would
  *     continue to grow.
  *     For the case where there are a few inserts/updates, a higher value will impact
  *     performance and waste disk space for each commit call without any added
  *     benefits.
  *     Background:
  *       For data retrieval ArangoSearch views follow the concept of
  *       "eventually-consistent", i.e. eventually all the data in ArangoDB will be
  *       matched by corresponding query expressions.
  *       The concept of ArangoSearch view "commit" operation is introduced to
  *       control the upper-bound on the time until document addition/removals are
  *       actually reflected by corresponding query expressions.
  *       Once a "commit" operation is complete all documents added/removed prior to
  *       the start of the "commit" operation will be reflected by queries invoked in
  *       subsequent ArangoDB transactions, in-progress ArangoDB transactions will
  *       still continue to return a repeatable-read state.
  *     - **links**:
  *       - **[collection-name]**:
  *         - **analyzers** (string): The list of analyzers to be used for indexing of string values
  *       (default: ["identity"]).
  *         - **fields**:
  *           - **field-name** (object): This is a recursive structure for the specific attribute path, potentially
  *        containing any of the following attributes:
  *        *analyzers*, *includeAllFields*, *trackListPositions*, *storeValues*
  *        Any attributes not specified are inherited from the parent.
  *         - **includeAllFields**: The flag determines whether or not to index all fields on a particular level of
  *       depth (default: false).
  *         - **trackListPositions**: The flag determines whether or not values in a lists should be treated separate
  *       (default: false).
  *         - **storeValues**: How should the view track the attribute values, this setting allows for
  *       additional value retrieval optimizations, one of:
  *       - *none*: Do not store values by the view
  *       - *id*: Store only information about value presence, to allow use of the EXISTS() function
  *       (default "none").
  *     - **consolidationIntervalMsec**: Wait at least this many milliseconds between applying 'consolidationPolicy' to
  *     consolidate view data store and possibly release space on the filesystem
  *     (default: 60000, to disable use: 0).
  *     For the case where there are a lot of data modification operations, a higher
  *     value could potentially have the data store consume more space and file handles.
  *     For the case where there are a few data modification operations, a lower value
  *     will impact performance due to no segment candidates available for
  *     consolidation.
  *     Background:
  *       For data modification ArangoSearch views follow the concept of a
  *       "versioned data store". Thus old versions of data may be removed once there
  *       are no longer any users of the old data. The frequency of the cleanup and
  *       compaction operations are governed by 'consolidationIntervalMsec' and the
  *       candidates for compaction are selected via 'consolidationPolicy'.
  *     - **cleanupIntervalStep**: Wait at least this many commits between removing unused files in the
  *     ArangoSearch data directory (default: 10, to disable use: 0).
  *     For the case where the consolidation policies merge segments often (i.e. a lot
  *     of commit+consolidate), a lower value will cause a lot of disk space to be
  *     wasted.
  *     For the case where the consolidation policies rarely merge segments (i.e. few
  *     inserts/deletes), a higher value will impact performance without any added
  *     benefits.
  *     Background:
  *       With every "commit" or "consolidate" operation a new state of the view
  *       internal data-structures is created on disk.
  *       Old states/snapshots are released once there are no longer any users
  *       remaining.
  *       However, the files for the released states/snapshots are left on disk, and
  *       only removed by "cleanup" operation.
  *     - **consolidationPolicy**:
  * 
  * 
  * 
  * 
  * Creates a new view with a given name and properties if it does not already
  * exist.
  * 
  * **Note**: view can't be created with the links. Please use PUT/PATCH
  * for links management.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/view</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"testViewBasics"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"arangosearch"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/104460"</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"104460"</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"testViewBasics"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"arangosearch"</span>, 
  * </code><code>  <span class="hljs-string">"cleanupIntervalStep"</span> : <span class="hljs-number">10</span>, 
  * </code><code>  <span class="hljs-string">"commitIntervalMsec"</span> : <span class="hljs-number">60000</span>, 
  * </code><code>  <span class="hljs-string">"consolidationIntervalMsec"</span> : <span class="hljs-number">60000</span>, 
  * </code><code>  <span class="hljs-string">"consolidationPolicy"</span> : { 
  * </code><code>    <span class="hljs-string">"type"</span> : <span class="hljs-string">"bytes_accum"</span>, 
  * </code><code>    <span class="hljs-string">"threshold"</span> : <span class="hljs-number">0.10000000149011612</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"writebufferActive"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"writebufferIdle"</span> : <span class="hljs-number">64</span>, 
  * </code><code>  <span class="hljs-string">"writebufferSizeMax"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>  <span class="hljs-string">"links"</span> : { 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def post(body: PostAPIView): Future[ArangoResponse] = client
    .method(HttpMethod.Post)
    .path(path"/_db/_system/_api/view#arangosearch".withArguments(Map()))
    .restful[PostAPIView, ArangoResponse](body)
}