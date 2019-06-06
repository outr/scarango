package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
object APIGharialGraphEdge {
  /**
  * Lists all edge collections within this graph.
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * Is returned if the edge definitions could be listed.
  * 
  * - **code**: The response code.
  * - **collections** (string): The list of all vertex collections within this graph.
  * Includes collections in edgeDefinitions as well as orphans.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is false in this response.
  * 
  * 
  * **HTTP 404**
  * *A json document with these Properties is returned:*
  * 
  * Returned if no graph with this name could be found.
  * 
  * - **errorMessage**: A message created for this error.
  * - **errorNum**: ArangoDB error number for the error that occured.
  * - **code**: The response code.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is true in this response.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/gharial/social/edge</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"collections"</span> : [ 
  * </code><code>    <span class="hljs-string">"relation"</span> 
  * </code><code>  ] 
  * </code><code>}
  * </code></pre>
  */
  def get(client: HttpClient, graph: String): Future[GeneralGraphListEdgeHttpExamplesRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_api/gharial/{graph}/edge".withArguments(Map("graph" -> graph)), append = true)
    .call[GeneralGraphListEdgeHttpExamplesRc200]

  /**
  * Adds an additional edge definition to the graph.
  * 
  * This edge definition has to contain a *collection* and an array of
  * each *from* and *to* vertex collections.  An edge definition can only
  * be added if this definition is either not used in any other graph, or
  * it is used with exactly the same definition. It is not possible to
  * store a definition "e" from "v1" to "v2" in the one graph, and "e"
  * from "v2" to "v1" in the other graph.
  * 
  * 
  * **A JSON object with these properties is required:**
  * 
  *   - **to** (string): One or many vertex collections that can contain target vertices.
  *   - **from** (string): One or many vertex collections that can contain source vertices.
  *   - **collection**: The name of the edge collection to be used.
  * 
  * 
  * 
  * **HTTP 201**
  * *A json document with these Properties is returned:*
  * 
  * Returned if the definition could be added successfully and
  * waitForSync is enabled for the `_graphs` collection.
  * The response body contains the graph configuration that has been stored.
  * 
  * - **graph**:
  *   - **smartGraphAttribute**: The name of the sharding attribute in smart graph case (Enterprise Edition only)
  *   - **replicationFactor**: The replication factor used for every new collection in the graph.
  *   - **orphanCollections** (string): An array of additional vertex collections.
  *    Documents within these collections do not have edges within this graph.
  *   - **name**: The name of the graph.
  *   - **_rev**: The revision of this graph. Can be used to make sure to not override
  *    concurrent modifications to this graph.
  *   - **numberOfShards**: Number of shards created for every new collection in the graph.
  *   - **isSmart**: Flag if the graph is a SmartGraph (Enterprise Edition only) or not.
  *   - **_id**: The internal id value of this graph. 
  *   - **edgeDefinitions**: An array of definitions for the relations of the graph.
  *    Each has the following type:
  *     - **to** (string): List of vertex collection names.
  *     Edges in collection can only be inserted if their _to is in any of the collections here.
  *     - **from** (string): List of vertex collection names.
  *     Edges in collection can only be inserted if their _from is in any of the collections here.
  *     - **collection**: Name of the edge collection, where the edge are stored in.
  * - **code**: The response code.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is false in this response.
  * 
  * 
  * **HTTP 202**
  * *A json document with these Properties is returned:*
  * 
  * Returned if the definition could be added successfully and
  * waitForSync is disabled for the `_graphs` collection.
  * The response body contains the graph configuration that has been stored.
  * 
  * - **graph**:
  *   - **smartGraphAttribute**: The name of the sharding attribute in smart graph case (Enterprise Edition only)
  *   - **replicationFactor**: The replication factor used for every new collection in the graph.
  *   - **orphanCollections** (string): An array of additional vertex collections.
  *    Documents within these collections do not have edges within this graph.
  *   - **name**: The name of the graph.
  *   - **_rev**: The revision of this graph. Can be used to make sure to not override
  *    concurrent modifications to this graph.
  *   - **numberOfShards**: Number of shards created for every new collection in the graph.
  *   - **isSmart**: Flag if the graph is a SmartGraph (Enterprise Edition only) or not.
  *   - **_id**: The internal id value of this graph. 
  *   - **edgeDefinitions**: An array of definitions for the relations of the graph.
  *    Each has the following type:
  *     - **to** (string): List of vertex collection names.
  *     Edges in collection can only be inserted if their _to is in any of the collections here.
  *     - **from** (string): List of vertex collection names.
  *     Edges in collection can only be inserted if their _from is in any of the collections here.
  *     - **collection**: Name of the edge collection, where the edge are stored in.
  * - **code**: The response code.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is false in this response.
  * 
  * 
  * **HTTP 400**
  * *A json document with these Properties is returned:*
  * 
  * Returned if the definition could not be added.
  * This could be because it is ill-formed, or
  * if the definition is used in an other graph with a different signature.
  * 
  * - **errorMessage**: A message created for this error.
  * - **errorNum**: ArangoDB error number for the error that occured.
  * - **code**: The response code.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is true in this response.
  * 
  * 
  * **HTTP 403**
  * *A json document with these Properties is returned:*
  * 
  * Returned if your user has insufficient rights.
  * In order to modify a graph you at least need to have the following privileges:
  *   1. `Administrate` access on the Database.
  * 
  * - **errorMessage**: A message created for this error.
  * - **errorNum**: ArangoDB error number for the error that occured.
  * - **code**: The response code.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is true in this response.
  * 
  * 
  * **HTTP 404**
  * *A json document with these Properties is returned:*
  * 
  * Returned if no graph with this name could be found.
  * 
  * - **errorMessage**: A message created for this error.
  * - **errorNum**: ArangoDB error number for the error that occured.
  * - **code**: The response code.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is true in this response.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/gharial/social/edge</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"works_in"</span>, 
  * </code><code>  <span class="hljs-string">"from"</span> : [ 
  * </code><code>    <span class="hljs-string">"female"</span>, 
  * </code><code>    <span class="hljs-string">"male"</span> 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"to"</span> : [ 
  * </code><code>    <span class="hljs-string">"city"</span> 
  * </code><code>  ] 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Accepted
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: _YOn1Goy--B
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">202</span>, 
  * </code><code>  <span class="hljs-string">"graph"</span> : { 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"social"</span>, 
  * </code><code>    <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>    <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">1</span>, 
  * </code><code>    <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>    <span class="hljs-string">"edgeDefinitions"</span> : [ 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"collection"</span> : <span class="hljs-string">"relation"</span>, 
  * </code><code>        <span class="hljs-string">"from"</span> : [ 
  * </code><code>          <span class="hljs-string">"female"</span>, 
  * </code><code>          <span class="hljs-string">"male"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"to"</span> : [ 
  * </code><code>          <span class="hljs-string">"female"</span>, 
  * </code><code>          <span class="hljs-string">"male"</span> 
  * </code><code>        ] 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"collection"</span> : <span class="hljs-string">"works_in"</span>, 
  * </code><code>        <span class="hljs-string">"from"</span> : [ 
  * </code><code>          <span class="hljs-string">"female"</span>, 
  * </code><code>          <span class="hljs-string">"male"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"to"</span> : [ 
  * </code><code>          <span class="hljs-string">"city"</span> 
  * </code><code>        ] 
  * </code><code>      } 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"orphanCollections"</span> : [ ], 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Goy--B"</span>, 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"_graphs/social"</span>, 
  * </code><code>    <span class="hljs-string">"name"</span> : <span class="hljs-string">"social"</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def post(client: HttpClient, graph: String, body: GeneralGraphEdgeDefinitionAddHttpExamples): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/gharial/{graph}/edge".withArguments(Map("graph" -> graph)), append = true)
    .restful[GeneralGraphEdgeDefinitionAddHttpExamples, Json](body)
}