package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APICollection {
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
  def get(client: HttpClient, excludeSystem: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/collection", append = true) 
    .param[Option[Boolean]]("excludeSystem", excludeSystem, None)
    .call[Json]

  /**
  * Creates a new collection with a given name. The request must contain an
  * object with the following attributes.
  * 
  * 
  * **A JSON object with these properties is required:**
  * 
  *   - **journalSize**: The maximal size of a journal or datafile in bytes. The value
  *    must be at least `1048576` (1 MiB). (The default is a configuration parameter)
  *    This option is meaningful for the MMFiles storage engine only.
  *   - **replicationFactor**: (The default is *1*): in a cluster, this attribute determines how many copies
  *    of each shard are kept on different DBServers. The value 1 means that only one
  *    copy (no synchronous replication) is kept. A value of k means that k-1 replicas
  *    are kept. Any two copies reside on different DBServers. Replication between them is 
  *    synchronous, that is, every write operation to the "leader" copy will be replicated 
  *    to all "follower" replicas, before the write operation is reported successful.
  *    If a server fails, this is detected automatically and one of the servers holding 
  *    copies take over, usually without an error being reported.
  *   - **keyOptions**:
  *     - **allowUserKeys**: if set to *true*, then it is allowed to supply own key values in the
  *     *_key* attribute of a document. If set to *false*, then the key generator
  *     will solely be responsible for generating keys and supplying own key values
  *     in the *_key* attribute of documents is considered an error.
  *     - **type**: specifies the type of the key generator. The currently available generators are
  *     *traditional*, *autoincrement*, *uuid* and *padded*.
  *     The *traditional* key generator generates numerical keys in ascending order.
  *     The *autoincrement* key generator generates numerical keys in ascending order, 
  *     the inital offset and the spacing can be configured
  *     The *padded* key generator generates keys of a fixed length (16 bytes) in
  *     ascending lexicographical sort order. This is ideal for usage with the _RocksDB_
  *     engine, which will slightly benefit keys that are inserted in lexicographically
  *     ascending order. The key generator can be used in a single-server or cluster.
  *     The *uuid* key generator generates universally unique 128 bit keys, which 
  *     are stored in hexadecimal human-readable format. This key generator can be used
  *     in a single-server or cluster to generate "seemingly random" keys. The keys 
  *     produced by this key generator are not lexicographically sorted.
  *     - **increment**: increment value for *autoincrement* key generator. Not used for other key
  *     generator types.
  *     - **offset**: Initial offset value for *autoincrement* key generator.
  *     Not used for other key generator types.
  *   - **name**: The name of the collection.
  *   - **waitForSync**: If *true* then the data is synchronized to disk before returning from a
  *    document create, update, replace or removal operation. (default: false)
  *   - **doCompact**: whether or not the collection will be compacted (default is *true*)
  *    This option is meaningful for the MMFiles storage engine only.
  *   - **shardingStrategy**: This attribute specifies the name of the sharding strategy to use for 
  *    the collection. Since ArangoDB 3.4 there are different sharding strategies 
  *    to select from when creating a new collection. The selected *shardingStrategy* 
  *    value will remain fixed for the collection and cannot be changed afterwards. 
  *    This is important to make the collection keep its sharding settings and
  *    always find documents already distributed to shards using the same
  *    initial sharding algorithm.
  *    The available sharding strategies are:
  *    - `community-compat`: default sharding used by ArangoDB
  *      Community Edition before version 3.4
  *    - `enterprise-compat`: default sharding used by ArangoDB
  *      Enterprise Edition before version 3.4
  *    - `enterprise-smart-edge-compat`: default sharding used by smart edge
  *      collections in ArangoDB Enterprise Edition before version 3.4
  *    - `hash`: default sharding used for new collections starting from version 3.4
  *      (excluding smart edge collections)
  *    - `enterprise-hash-smart-edge`: default sharding used for new
  *      smart edge collections starting from version 3.4
  *    If no sharding strategy is specified, the default will be *hash* for
  *    all collections, and *enterprise-hash-smart-edge* for all smart edge
  *    collections (requires the *Enterprise Edition* of ArangoDB). 
  *    Manually overriding the sharding strategy does not yet provide a 
  *    benefit, but it may later in case other sharding strategies are added.
  *   - **isVolatile**: If *true* then the collection data is kept in-memory only and not made persistent.
  *    Unloading the collection will cause the collection data to be discarded. Stopping
  *    or re-starting the server will also cause full loss of data in the
  *    collection. Setting this option will make the resulting collection be
  *    slightly faster than regular collections because ArangoDB does not
  *    enforce any synchronization to disk and does not calculate any CRC
  *    checksums for datafiles (as there are no datafiles). This option
  *    should therefore be used for cache-type collections only, and not
  *    for data that cannot be re-created otherwise.
  *    (The default is *false*)
  *    This option is meaningful for the MMFiles storage engine only.
  *   - **shardKeys**: (The default is *[ "_key" ]*): in a cluster, this attribute determines
  *    which document attributes are used to determine the target shard for documents.
  *    Documents are sent to shards based on the values of their shard key attributes.
  *    The values of all shard key attributes in a document are hashed,
  *    and the hash value is used to determine the target shard.
  *    **Note**: Values of shard key attributes cannot be changed once set.
  *      This option is meaningless in a single server setup.
  *   - **smartJoinAttribute**: In an *Enterprise Edition* cluster, this attribute determines an attribute
  *    of the collection that must contain the shard key value of the referred-to 
  *    smart join collection. Additionally, the shard key for a document in this 
  *    collection must contain the value of this attribute, followed by a colon, 
  *    followed by the actual primary key of the document.
  *    This feature can only be used in the *Enterprise Edition* and requires the
  *    *distributeShardsLike* attribute of the collection to be set to the name
  *    of another collection. It also requires the *shardKeys* attribute of the
  *    collection to be set to a single shard key attribute, with an additional ':'
  *    at the end.
  *    A further restriction is that whenever documents are stored or updated in the 
  *    collection, the value stored in the *smartJoinAttribute* must be a string.
  *   - **numberOfShards**: (The default is *1*): in a cluster, this value determines the
  *    number of shards to create for the collection. In a single
  *    server setup, this option is meaningless.
  *   - **isSystem**: If *true*, create a  system collection. In this case *collection-name*
  *    should start with an underscore. End users should normally create non-system
  *    collections only. API implementors may be required to create system
  *    collections in very special occasions, but normally a regular collection will do.
  *    (The default is *false*)
  *   - **type**: (The default is *2*): the type of the collection to create.
  *    The following values for *type* are valid:
  *    - *2*: document collection
  *    - *3*: edge collection
  *   - **indexBuckets**: The number of buckets into which indexes using a hash
  *    table are split. The default is 16 and this number has to be a
  *    power of 2 and less than or equal to 1024.
  *    For very large collections one should increase this to avoid long pauses
  *    when the hash table has to be initially built or resized, since buckets
  *    are resized individually and can be initially built in parallel. For
  *    example, 64 might be a sensible value for a collection with 100
  *    000 000 documents. Currently, only the edge index respects this
  *    value, but other index types might follow in future ArangoDB versions.
  *    Changes (see below) are applied when the collection is loaded the next
  *    time.
  *    This option is meaningful for the MMFiles storage engine only.
  *   - **distributeShardsLike**: (The default is *""*): in an Enterprise Edition cluster, this attribute binds
  *    the specifics of sharding for the newly created collection to follow that of a
  *    specified existing collection.
  *    **Note**: Using this parameter has consequences for the prototype
  *    collection. It can no longer be dropped, before the sharding-imitating
  *    collections are dropped. Equally, backups and restores of imitating
  *    collections alone will generate warnings (which can be overridden)
  *    about missing sharding prototype.
  * 
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * - **smartGraphAttribute**: Attribute that is used in smart graphs, *Cluster specific attribute.*
  * - **journalSize**: The maximal size setting for journals / datafiles
  * in bytes. This option is only present for the MMFiles storage engine.
  * - **replicationFactor**: contains how many copies of each shard are kept on different DBServers.; *Cluster specific attribute.*
  * - **keyOptions**:
  *   - **lastValue**: 
  *   - **allowUserKeys**: if set to *true*, then it is allowed to supply
  *    own key values in the *_key* attribute of a document. If set to
  *    *false*, then the key generator is solely responsible for
  *    generating keys and supplying own key values in the *_key* attribute
  *    of documents is considered an error.
  *   - **type**: specifies the type of the key generator. The currently
  *    available generators are *traditional*, *autoincrement*, *uuid*
  *    and *padded*.
  * - **name**: literal name of this collection
  * - **waitForSync**: If *true* then creating, changing or removing
  * documents will wait until the data has been synchronized to disk.
  * - **doCompact**: Whether or not the collection will be compacted.
  * This option is only present for the MMFiles storage engine.
  * - **shardingStrategy**: the sharding strategy selected for the collection; *Cluster specific attribute.*
  * One of 'hash' or 'enterprise-hash-smart-edge'
  * - **isVolatile**: If *true* then the collection data will be
  * kept in memory only and ArangoDB will not write or sync the data
  * to disk. This option is only present for the MMFiles storage engine.
  * - **indexBuckets**: the number of index buckets
  * *Only relevant for the MMFiles storage engine*
  * - **numberOfShards**: The number of shards of the collection; *Cluster specific attribute.*
  * - **status**: corrosponds to **statusString**; *Only relevant for the MMFiles storage engine*
  *   - 0: "unknown" - may be corrupted
  *   - 1: (deprecated, maps to "unknown")
  *   - 2: "unloaded"
  *   - 3: "loaded"
  *   - 4: "unloading"
  *   - 5: "deleted"
  *   - 6: "loading"
  * - **statusString**: any of: ["unloaded", "loading", "loaded", "unloading", "deleted", "unknown"] *Only relevant for the MMFiles storage engine*
  * - **globallyUniqueId**: Unique identifier of the collection
  * - **id**: unique identifier of the collection; *deprecated*
  * - **isSystem**: true if this is a system collection; usually *name* will start with an underscore.
  * - **type**: The type of the collection:
  *   - 0: "unknown"
  *   - 2: regular document collection
  *   - 3: edge collection
  * - **shardKeys** (string): contains the names of document attributes that are used to
  * determine the target shard for documents; *Cluster specific attribute.*
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
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/collection</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"testCollectionBasics"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>  <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>  <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>    <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>    <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/102801"</span>, 
  * </code><code>  <span class="hljs-string">"statusString"</span> : <span class="hljs-string">"loaded"</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"102801"</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"testCollectionBasics"</span>, 
  * </code><code>  <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>  <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span> 
  * </code><code>}
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/collection</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"testCollectionEdges"</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">3</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">3</span>, 
  * </code><code>  <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>  <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>  <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>    <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>    <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/102807"</span>, 
  * </code><code>  <span class="hljs-string">"statusString"</span> : <span class="hljs-string">"loaded"</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"102807"</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"testCollectionEdges"</span>, 
  * </code><code>  <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>  <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/collection</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"testCollectionUsers"</span>, 
  * </code><code>  <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>    <span class="hljs-string">"type"</span> : <span class="hljs-string">"autoincrement"</span>, 
  * </code><code>    <span class="hljs-string">"increment"</span> : <span class="hljs-number">5</span>, 
  * </code><code>    <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span> 
  * </code><code>  } 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>  <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>  <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>    <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"type"</span> : <span class="hljs-string">"autoincrement"</span>, 
  * </code><code>    <span class="hljs-string">"offset"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"increment"</span> : <span class="hljs-number">5</span>, 
  * </code><code>    <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/102822"</span>, 
  * </code><code>  <span class="hljs-string">"statusString"</span> : <span class="hljs-string">"loaded"</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"102822"</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"testCollectionUsers"</span>, 
  * </code><code>  <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>  <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span> 
  * </code><code>}
  * </code></pre>
  */
  def post(client: HttpClient, body: PostAPICollection, waitForSyncReplication: Option[Int] = None, enforceReplicationFactor: Option[Int] = None)(implicit ec: ExecutionContext): Future[CollectionInfo] = client
    .method(HttpMethod.Post)
    .path(path"/_api/collection", append = true) 
    .param[Option[Int]]("waitForSyncReplication", waitForSyncReplication, None)
    .param[Option[Int]]("enforceReplicationFactor", enforceReplicationFactor, None)
    .restful[PostAPICollection, CollectionInfo](body)
}