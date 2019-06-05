package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APICollectionCollectionNameProperties(client: HttpClient) {
  /**
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
  * 
  * 
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
  *  Using an identifier:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/collection/103234/properties</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>location: <span class="hljs-regexp">/_api/</span>collection/<span class="hljs-number">103234</span>/properties
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>  <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>  <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>    <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>    <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/103234"</span>, 
  * </code><code>  <span class="hljs-string">"statusString"</span> : <span class="hljs-string">"loaded"</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"103234"</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"products"</span>, 
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
  *  Using a name:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/collection/products/properties</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>location: <span class="hljs-regexp">/_api/</span>collection/products/properties
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>  <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>  <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>    <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>    <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/103246"</span>, 
  * </code><code>  <span class="hljs-string">"statusString"</span> : <span class="hljs-string">"loaded"</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"103246"</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>  <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span> 
  * </code><code>}
  * </code></pre>
  */
  def get(collectionName: String): Future[CollectionInfo] = client
    .method(HttpMethod.Get)
    .path(path"/_api/collection/{collection-name}/properties".withArguments(Map("collection-name" -> collectionName)), append = true)
    .call[CollectionInfo]

  /**
  * Changes the properties of a collection. Expects an object with the
  * attribute(s)
  * 
  * - *waitForSync*: If *true* then creating or changing a
  *   document will wait until the data has been synchronized to disk.
  * 
  * - *journalSize*: The maximal size of a journal or datafile in bytes. 
  *   The value must be at least `1048576` (1 MB). Note that when
  *   changing the journalSize value, it will only have an effect for
  *   additional journals or datafiles that are created. Already
  *   existing journals or datafiles will not be affected.
  * 
  * On success an object with the following attributes is returned:
  * 
  * - *id*: The identifier of the collection.
  * 
  * - *name*: The name of the collection.
  * 
  * - *waitForSync*: The new value.
  * 
  * - *journalSize*: The new value.
  * 
  * - *status*: The status of the collection as number.
  * 
  * - *type*: The collection type. Valid types are:
  *   - 2: document collection
  *   - 3: edges collection
  * 
  * - *isSystem*: If *true* then the collection is a system collection.
  * 
  * - *isVolatile*: If *true* then the collection data will be
  *   kept in memory only and ArangoDB will not write or sync the data
  *   to disk.
  * 
  * - *doCompact*: Whether or not the collection will be compacted.
  * 
  * - *keyOptions*: JSON object which contains key generation options:
  *   - *type*: specifies the type of the key generator. The currently
  *     available generators are *traditional*, *autoincrement*, *uuid*
  *     and *padded*.
  *   - *allowUserKeys*: if set to *true*, then it is allowed to supply
  *     own key values in the *_key* attribute of a document. If set to
  *     *false*, then the key generator is solely responsible for
  *     generating keys and supplying own key values in the *_key* attribute
  *     of documents is considered an error.
  * 
  * **Note**: except for *waitForSync*, *journalSize* and *name*, collection
  * properties **cannot be changed** once a collection is created. To rename
  * a collection, the rename endpoint must be used.
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/collection/products/properties</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">true</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>location: <span class="hljs-regexp">/_api/</span>collection/products/properties
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>  <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>  <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>    <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>    <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">0</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/103295"</span>, 
  * </code><code>  <span class="hljs-string">"statusString"</span> : <span class="hljs-string">"loaded"</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"103295"</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span>, 
  * </code><code>  <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(collectionName: String): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/collection/{collection-name}/properties".withArguments(Map("collection-name" -> collectionName)), append = true)
    .call[Json]
}