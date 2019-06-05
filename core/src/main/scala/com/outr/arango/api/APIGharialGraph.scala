package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIGharialGraph(client: HttpClient) {
  /**
  * Drops an existing graph object by name.
  * Optionally all collections not used by other graphs
  * can be dropped as well.
  * 
  * 
  * **HTTP 403**
  * *A json document with these Properties is returned:*
  * 
  * Returned if your user has insufficient rights.
  * In order to drop a graph you at least need to have the following privileges:
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/gharial/social?dropCollections=<span class="hljs-literal">true</span></span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Accepted
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">202</span>, 
  * </code><code>  <span class="hljs-string">"removed"</span> : <span class="hljs-literal">true</span> 
  * </code><code>}
  * </code></pre>
  */
  def delete(graph: String, dropCollections: Option[Boolean] = None): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/gharial/{graph}".withArguments(Map("graph" -> graph)), append = true)
    .param[Option[Boolean]]("dropCollections", dropCollections, None)
    .call[Json]

  /**
  * Selects information for a given graph.
  * Will return the edge definitions as well as the orphan collections.
  * Or returns a 404 if the graph does not exist.
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * Returns the graph if it could be found.
  * The result will have the following format:
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/gharial/myGraph</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"graph"</span> : { 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"myGraph"</span>, 
  * </code><code>    <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">1</span>, 
  * </code><code>    <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">1</span>, 
  * </code><code>    <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>    <span class="hljs-string">"edgeDefinitions"</span> : [ 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"collection"</span> : <span class="hljs-string">"edges"</span>, 
  * </code><code>        <span class="hljs-string">"from"</span> : [ 
  * </code><code>          <span class="hljs-string">"startVertices"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"to"</span> : [ 
  * </code><code>          <span class="hljs-string">"endVertices"</span> 
  * </code><code>        ] 
  * </code><code>      } 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"orphanCollections"</span> : [ ], 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1HMu--B"</span>, 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"_graphs/myGraph"</span>, 
  * </code><code>    <span class="hljs-string">"name"</span> : <span class="hljs-string">"myGraph"</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def get(graph: String): Future[GeneralGraphGetHttpExamplesRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_api/gharial/{graph}".withArguments(Map("graph" -> graph)), append = true)
    .call[GeneralGraphGetHttpExamplesRc200]
}