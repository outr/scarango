package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APIIndexgeo(client: HttpClient) {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **fields** (string): An array with one or two attribute paths.
  *    If it is an array with one attribute path *location*, then a geo-spatial
  *    index on all documents is created using *location* as path to the
  *    coordinates. The value of the attribute must be an array with at least two
  *    double values. The array must contain the latitude (first value) and the
  *    longitude (second value). All documents, which do not have the attribute
  *    path or with value that are not suitable, are ignored.
  *    If it is an array with two attribute paths *latitude* and *longitude*,
  *    then a geo-spatial index on all documents is created using *latitude*
  *    and *longitude* as paths the latitude and the longitude. The value of
  *    the attribute *latitude* and of the attribute *longitude* must a
  *    double. All documents, which do not have the attribute paths or which
  *    values are not suitable, are ignored.
  *   - **type**: must be equal to *"geo"*.
  *   - **geoJson**: If a geo-spatial index on a *location* is constructed
  *    and *geoJson* is *true*, then the order within the array is longitude
  *    followed by latitude. This corresponds to the format described in
  *    http://geojson.org/geojson-spec.html#positions
  * 
  * 
  * 
  * 
  * **NOTE** Swagger examples won't work due to the anchor.
  * 
  * 
  * Creates a geo-spatial index in the collection *collection-name*, if
  * it does not already exist. Expects an object containing the index details.
  * 
  * Geo indexes are always sparse, meaning that documents that do not contain
  * the index attributes or have non-numeric values in the index attributes
  * will not be indexed.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Creating a geo index with a location attribute
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/index?collection=products</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"geo"</span>, 
  * </code><code>  <span class="hljs-string">"fields"</span> : [ 
  * </code><code>    <span class="hljs-string">"b"</span> 
  * </code><code>  ] 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"bestIndexedLevel"</span> : <span class="hljs-number">17</span>, 
  * </code><code>  <span class="hljs-string">"fields"</span> : [ 
  * </code><code>    <span class="hljs-string">"b"</span> 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"geoJson"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"products/104760"</span>, 
  * </code><code>  <span class="hljs-string">"isNewlyCreated"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"maxNumCoverCells"</span> : <span class="hljs-number">8</span>, 
  * </code><code>  <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"geo"</span>, 
  * </code><code>  <span class="hljs-string">"unique"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"worstIndexedLevel"</span> : <span class="hljs-number">4</span>, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Creating a geo index with latitude and longitude attributes
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/index?collection=products</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"geo"</span>, 
  * </code><code>  <span class="hljs-string">"fields"</span> : [ 
  * </code><code>    <span class="hljs-string">"e"</span>, 
  * </code><code>    <span class="hljs-string">"f"</span> 
  * </code><code>  ] 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"bestIndexedLevel"</span> : <span class="hljs-number">17</span>, 
  * </code><code>  <span class="hljs-string">"fields"</span> : [ 
  * </code><code>    <span class="hljs-string">"e"</span>, 
  * </code><code>    <span class="hljs-string">"f"</span> 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"geoJson"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"products/104746"</span>, 
  * </code><code>  <span class="hljs-string">"isNewlyCreated"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"maxNumCoverCells"</span> : <span class="hljs-number">8</span>, 
  * </code><code>  <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"geo"</span>, 
  * </code><code>  <span class="hljs-string">"unique"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"worstIndexedLevel"</span> : <span class="hljs-number">4</span>, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code></pre>
  */
  def post(collection: String, body: PostAPIIndexGeo): Future[ArangoResponse] = client
    .method(HttpMethod.Post)
    .path(path"/_db/_system/_api/index#geo".withArguments(Map()))
    .params("collection" -> collection.toString)
    .restful[PostAPIIndexGeo, ArangoResponse](body)
}