package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiCollection{CollectionName}PropertiesPut(client: HttpClient) {
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
  def put(collectionName: String): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .params("collection-name" -> collection-name.toString)
    .call[ArangoResponse]
}