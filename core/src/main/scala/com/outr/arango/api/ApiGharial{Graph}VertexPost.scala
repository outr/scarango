package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiGharial{Graph}VertexPost(client: HttpClient) {
  /**
  * Adds a vertex collection to the set of orphan collections of the graph.
  * If the collection does not exist, it will be created.
  * 
  * 
  * **HTTP 201**
  * *A json document with these Properties is returned:*
  * 
  * Is returned if the collection could be created and waitForSync is enabled
  * for the `_graphs` collection, or given in the request.
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
  * Is returned if the collection could be created and waitForSync is disabled
  * for the `_graphs` collection, or given in the request.
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
  * Returned if the request is in an invalid format.
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
  *   2. `Read Only` access on every collection used within this graph.
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/gharial/social/vertex</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"otherVertices"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Accepted
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: _YOn1Gwm--B
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
  * </code><code>      } 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"orphanCollections"</span> : [ 
  * </code><code>      <span class="hljs-string">"otherVertices"</span> 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Gwm--B"</span>, 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"_graphs/social"</span>, 
  * </code><code>    <span class="hljs-string">"name"</span> : <span class="hljs-string">"social"</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def post(graph: String): Future[ArangoResponse] = client
    .method(HttpMethod.Post)
    .params("graph" -> graph.toString)
    .call[ArangoResponse]
}