package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APICollectionCollectionNameChecksum(client: HttpClient) {
  /**
  * Will calculate a checksum of the meta-data (keys and optionally revision ids) and
  * optionally the document data in the collection.
  * 
  * The checksum can be used to compare if two collections on different ArangoDB
  * instances contain the same contents. The current revision of the collection is
  * returned too so one can make sure the checksums are calculated for the same
  * state of data.
  * 
  * By default, the checksum will only be calculated on the *_key* system attribute
  * of the documents contained in the collection. For edge collections, the system
  * attributes *_from* and *_to* will also be included in the calculation.
  * 
  * By setting the optional query parameter *withRevisions* to *true*, then revision
  * ids (*_rev* system attributes) are included in the checksumming.
  * 
  * By providing the optional query parameter *withData* with a value of *true*,
  * the user-defined document attributes will be included in the calculation too.
  * **Note**: Including user-defined attributes will make the checksumming slower.
  * 
  * The response is a JSON object with the following attributes:
  * 
  * - *checksum*: The calculated checksum as a number.
  * 
  * - *revision*: The collection revision id as a string.
  * 
  * **Note**: this method is not available in a cluster.
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
  *  Retrieving the checksum of a collection:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/collection/products/checksum</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>location: <span class="hljs-regexp">/_api/</span>collection/products/checksum
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/102861"</span>, 
  * </code><code>  <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"102861"</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"revision"</span> : <span class="hljs-string">"_YOn1KHS--_"</span>, 
  * </code><code>  <span class="hljs-string">"checksum"</span> : <span class="hljs-string">"2089246606277080887"</span>, 
  * </code><code>  <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Retrieving the checksum of a collection including the collection data,
  * but not the revisions:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/collection/products/checksum?withRevisions=<span class="hljs-literal">false</span>&amp;withData=<span class="hljs-literal">true</span></span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>location: <span class="hljs-regexp">/_api/</span>collection/products/checksum
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"globallyUniqueId"</span> : <span class="hljs-string">"h8B2B671BCFD0/102877"</span>, 
  * </code><code>  <span class="hljs-string">"isSystem"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"102877"</span>, 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"revision"</span> : <span class="hljs-string">"_YOn1KJC--B"</span>, 
  * </code><code>  <span class="hljs-string">"checksum"</span> : <span class="hljs-string">"6947804677053586772"</span>, 
  * </code><code>  <span class="hljs-string">"status"</span> : <span class="hljs-number">3</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-number">2</span> 
  * </code><code>}
  * </code></pre>
  */
  def get(collectionName: String, withRevisions: Option[Boolean] = None, withData: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_api/collection/{collection-name}/checksum".withArguments(Map("collection-name" -> collectionName)))
    .param[Option[Boolean]]("withRevisions", withRevisions, None)
    .param[Option[Boolean]]("withData", withData, None)
    .call[ArangoResponse]
}