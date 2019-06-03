package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiGharialGet(client: HttpClient) {
  /**
  * Lists all graphs stored in this database.
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * Is returned if the module is available and the graphs could be listed.
  * 
  * - **graphs**: 
  *   - **graph**:
  *     - **smartGraphAttribute**: The name of the sharding attribute in smart graph case (Enterprise Edition only)
  *     - **replicationFactor**: The replication factor used for every new collection in the graph.
  *     - **orphanCollections** (string): An array of additional vertex collections.
  *     Documents within these collections do not have edges within this graph.
  *     - **name**: The name of the graph.
  *     - **_rev**: The revision of this graph. Can be used to make sure to not override
  *     concurrent modifications to this graph.
  *     - **numberOfShards**: Number of shards created for every new collection in the graph.
  *     - **isSmart**: Flag if the graph is a SmartGraph (Enterprise Edition only) or not.
  *     - **_id**: The internal id value of this graph. 
  *     - **edgeDefinitions**: An array of definitions for the relations of the graph.
  *     Each has the following type:
  *       - **to** (string): List of vertex collection names.
  *      Edges in collection can only be inserted if their _to is in any of the collections here.
  *       - **from** (string): List of vertex collection names.
  *      Edges in collection can only be inserted if their _from is in any of the collections here.
  *       - **collection**: Name of the edge collection, where the edge are stored in.
  * - **code**: The response code.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is false in this response.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/gharial</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"graphs"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"routeplanner"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"_graphs/routeplanner"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1HVm--B"</span>, 
  * </code><code>      <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>      <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">1</span>, 
  * </code><code>      <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>      <span class="hljs-string">"edgeDefinitions"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"collection"</span> : <span class="hljs-string">"frenchHighway"</span>, 
  * </code><code>          <span class="hljs-string">"from"</span> : [ 
  * </code><code>            <span class="hljs-string">"frenchCity"</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"to"</span> : [ 
  * </code><code>            <span class="hljs-string">"frenchCity"</span> 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"collection"</span> : <span class="hljs-string">"germanHighway"</span>, 
  * </code><code>          <span class="hljs-string">"from"</span> : [ 
  * </code><code>            <span class="hljs-string">"germanCity"</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"to"</span> : [ 
  * </code><code>            <span class="hljs-string">"germanCity"</span> 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"collection"</span> : <span class="hljs-string">"internationalHighway"</span>, 
  * </code><code>          <span class="hljs-string">"from"</span> : [ 
  * </code><code>            <span class="hljs-string">"frenchCity"</span>, 
  * </code><code>            <span class="hljs-string">"germanCity"</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"to"</span> : [ 
  * </code><code>            <span class="hljs-string">"frenchCity"</span>, 
  * </code><code>            <span class="hljs-string">"germanCity"</span> 
  * </code><code>          ] 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"orphanCollections"</span> : [ ] 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"social"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"_graphs/social"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1HT---B"</span>, 
  * </code><code>      <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>      <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">1</span>, 
  * </code><code>      <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>      <span class="hljs-string">"edgeDefinitions"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"collection"</span> : <span class="hljs-string">"relation"</span>, 
  * </code><code>          <span class="hljs-string">"from"</span> : [ 
  * </code><code>            <span class="hljs-string">"female"</span>, 
  * </code><code>            <span class="hljs-string">"male"</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"to"</span> : [ 
  * </code><code>            <span class="hljs-string">"female"</span>, 
  * </code><code>            <span class="hljs-string">"male"</span> 
  * </code><code>          ] 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"orphanCollections"</span> : [ ] 
  * </code><code>    } 
  * </code><code>  ] 
  * </code><code>}
  * </code></pre>
  */
  def get(): Future[GeneralGraphListHttpExamplesRc200] = client
    .method(HttpMethod.Get)
    .call[GeneralGraphListHttpExamplesRc200]
}