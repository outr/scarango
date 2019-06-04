package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APICollectionCollectionNameFigures(client: HttpClient) {
  /**
  * In addition to the above, the result also contains the number of documents
  * and additional statistical information about the collection.
  * **Note** : This will always load the collection into memory.
  * 
  * **Note**: collection data that are stored in the write-ahead log only are
  * not reported in the results. When the write-ahead log is collected, documents
  * might be added to journals and datafiles of the collection, which may modify
  * the figures of the collection.
  * 
  * Additionally, the filesizes of collection and index parameter JSON files are
  * not reported. These files should normally have a size of a few bytes
  * each. Please also note that the *fileSize* values are reported in bytes
  * and reflect the logical file sizes. Some filesystems may use optimizations
  * (e.g. sparse files) so that the actual physical file size is somewhat
  * different. Directories and sub-directories may also require space in the
  * file system, but this space is not reported in the *fileSize* results.
  * 
  * That means that the figures reported do not reflect the actual disk
  * usage of the collection with 100% accuracy. The actual disk usage of
  * a collection is normally slightly higher than the sum of the reported
  * *fileSize* values. Still the sum of the *fileSize* values can still be
  * used as a lower bound approximation of the disk usage.
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * Returns information about the collection:
  * 
  * - **count**: The number of documents currently present in the collection.
  * - **journalSize**: The maximal size of a journal or datafile in bytes.
  * - **figures**:
  *   - **datafiles**:
  *     - **count**: The number of datafiles.
  *     - **fileSize**: The total filesize of datafiles (in bytes).
  *   - **uncollectedLogfileEntries**: The number of markers in the write-ahead
  *    log for this collection that have not been transferred to journals or datafiles.
  *   - **documentReferences**: The number of references to documents in datafiles that JavaScript code 
  *    currently holds. This information can be used for debugging compaction and 
  *    unload issues.
  *   - **compactionStatus**:
  *     - **message**: The action that was performed when the compaction was last run for the collection. 
  *     This information can be used for debugging compaction issues.
  *     - **time**: The point in time the compaction for the collection was last executed. 
  *     This information can be used for debugging compaction issues.
  *   - **compactors**:
  *     - **count**: The number of compactor files.
  *     - **fileSize**: The total filesize of all compactor files (in bytes).
  *   - **dead**:
  *     - **count**: The number of dead documents. This includes document
  *     versions that have been deleted or replaced by a newer version. Documents
  *     deleted or replaced that are contained the write-ahead log only are not reported
  *     in this figure.
  *     - **deletion**: The total number of deletion markers. Deletion markers
  *     only contained in the write-ahead log are not reporting in this figure.
  *     - **size**: The total size in bytes used by all dead documents.
  *   - **indexes**:
  *     - **count**: The total number of indexes defined for the collection, including the pre-defined
  *     indexes (e.g. primary index).
  *     - **size**: The total memory allocated for indexes in bytes.
  *   - **readcache**:
  *     - **count**: The number of revisions of this collection stored in the document revisions cache.
  *     - **size**: The memory used for storing the revisions of this collection in the document 
  *     revisions cache (in bytes). This figure does not include the document data but 
  *     only mappings from document revision ids to cache entry locations.
  *   - **waitingFor**: An optional string value that contains information about which object type is at the 
  *    head of the collection's cleanup queue. This information can be used for debugging 
  *    compaction and unload issues.
  *   - **alive**:
  *     - **count**: The number of currently active documents in all datafiles
  *     and journals of the collection. Documents that are contained in the
  *     write-ahead log only are not reported in this figure.
  *     - **size**: The total size in bytes used by all active documents of
  *     the collection. Documents that are contained in the write-ahead log only are
  *     not reported in this figure.
  *   - **lastTick**: The tick of the last marker that was stored in a journal
  *    of the collection. This might be 0 if the collection does not yet have
  *    a journal.
  *   - **journals**:
  *     - **count**: The number of journal files.
  *     - **fileSize**: The total filesize of all journal files (in bytes).
  *   - **revisions**:
  *     - **count**: The number of revisions of this collection managed by the storage engine.
  *     - **size**: The memory used for storing the revisions of this collection in the storage 
  *     engine (in bytes). This figure does not include the document data but only mappings 
  *     from document revision ids to storage engine datafile positions.
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
  *  Using an identifier and requesting the figures of the collection:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/collection/products/figures</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>location: <span class="hljs-regexp">/_api/</span>collection/products/figures
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>  <span class="hljs-string">"journalSize"</span> : <span class="hljs-number">33554432</span>, 
  * </code><code>  <span class="hljs-string">"isVolatile"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"doCompact"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-number">1</span>, 
  * </code><code>  <span class="hljs-string">"waitForSync"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"figures"</span> : { 
  * </code><code>    <span class="hljs-string">"indexes"</span> : { 
  * </code><code>      <span class="hljs-string">"count"</span> : <span class="hljs-number">1</span>, 
  * </code><code>      <span class="hljs-string">"size"</span> : <span class="hljs-number">32128</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"documentReferences"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"waitingFor"</span> : <span class="hljs-string">"-"</span>, 
  * </code><code>    <span class="hljs-string">"alive"</span> : { 
  * </code><code>      <span class="hljs-string">"count"</span> : <span class="hljs-number">1</span>, 
  * </code><code>      <span class="hljs-string">"size"</span> : <span class="hljs-number">88</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"dead"</span> : { 
  * </code><code>      <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"size"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"deletion"</span> : <span class="hljs-number">0</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"compactionStatus"</span> : { 
  * </code><code>      <span class="hljs-string">"message"</span> : <span class="hljs-string">"skipped compaction because collection has no datafiles"</span>, 
  * </code><code>      <span class="hljs-string">"time"</span> : <span class="hljs-string">"2019-02-20T10:32:57Z"</span>, 
  * </code><code>      <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"filesCombined"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"bytesRead"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"bytesWritten"</span> : <span class="hljs-number">0</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"datafiles"</span> : { 
  * </code><code>      <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"fileSize"</span> : <span class="hljs-number">0</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"journals"</span> : { 
  * </code><code>      <span class="hljs-string">"count"</span> : <span class="hljs-number">1</span>, 
  * </code><code>      <span class="hljs-string">"fileSize"</span> : <span class="hljs-number">33554432</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"compactors"</span> : { 
  * </code><code>      <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"fileSize"</span> : <span class="hljs-number">0</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"revisions"</span> : { 
  * </code><code>      <span class="hljs-string">"count"</span> : <span class="hljs-number">1</span>, 
  * </code><code>      <span class="hljs-string">"size"</span> : <span class="hljs-number">48192</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"lastTick"</span> : <span class="hljs-number">103215</span>, 
  * </code><code>    <span class="hljs-string">"uncollectedLogfileEntries"</span> : <span class="hljs-number">0</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"keyOptions"</span> : { 
  * </code><code>    <span class="hljs-string">"allowUserKeys"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"type"</span> : <span class="hljs-string">"traditional"</span>, 
  * </code><code>    <span class="hljs-string">"lastValue"</span> : <span class="hljs-number">103213</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/103206"</span>, 
  * </code><code>  <span class="hljs-string">"statusString"</span> : <span class="hljs-string">"loaded"</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"103206"</span>, 
  * </code><code>  <span class="hljs-string">"indexBuckets"</span> : <span class="hljs-number">8</span> 
  * </code><code>}
  * </code></pre>
  */
  def get(collectionName: String): Future[GetAPICollectionFiguresRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_api/collection/{collection-name}/figures".withArguments(Map("collection-name" -> collectionName)))
    .call[GetAPICollectionFiguresRc200]
}