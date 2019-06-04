package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APICollectionCollectionNameLoadIndexesIntoMemory(client: HttpClient) {
  /**
  * This route tries to cache all index entries
  * of this collection into the main memory.
  * Therefore it iterates over all indexes of the collection
  * and stores the indexed values, not the entire document data,
  * in memory.
  * All lookups that could be found in the cache are much faster
  * than lookups not stored in the cache so you get a nice performance boost.
  * It is also guaranteed that the cache is consistent with the stored data.
  * 
  * For the time being this function is only useful on RocksDB storage engine,
  * as in MMFiles engine all indexes are in memory anyways.
  * 
  * On RocksDB this function honors all memory limits, if the indexes you want
  * to load are smaller than your memory limit this function guarantees that most
  * index values are cached.
  * If the index is larger than your memory limit this function will fill up values
  * up to this limit and for the time being there is no way to control which indexes
  * of the collection should have priority over others.
  * 
  * On sucess this function returns an object with attribute `result` set to `true`
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/collection/products/loadIndexesIntoMemory</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>location: <span class="hljs-regexp">/_api/</span>collection/products/loadIndexesIntoMemory
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"result"</span> : <span class="hljs-literal">true</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(collectionName: String): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .path(path"/_db/_system/_api/collection/{collection-name}/loadIndexesIntoMemory".withArguments(Map("collection-name" -> collectionName)))
    .call[ArangoResponse]
}