package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
object APIGharialGraphEdgeDefinition {
  /**
  * Remove one edge definition from the graph.  This will only remove the
  * edge collection, the vertex collections remain untouched and can still
  * be used in your queries.
  * 
  * 
  * **HTTP 201**
  * *A json document with these Properties is returned:*
  * 
  * Returned if the edge definition could be removed from the graph 
  * and waitForSync is true.
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
  * Returned if the edge definition could be removed from the graph and
  * waitForSync is false.
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
  * **HTTP 403**
  * *A json document with these Properties is returned:*
  * 
  * Returned if your user has insufficient rights.
  * In order to drop a vertex you at least need to have the following privileges:
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
  * Returned if no graph with this name could be found,
  * or if no edge definition with this name is found in the graph.
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/gharial/social/edge/relation</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Accepted
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: _YOn1HG---F
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
  * </code><code>    <span class="hljs-string">"edgeDefinitions"</span> : [ ], 
  * </code><code>    <span class="hljs-string">"orphanCollections"</span> : [ 
  * </code><code>      <span class="hljs-string">"female"</span>, 
  * </code><code>      <span class="hljs-string">"male"</span> 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1HG---F"</span>, 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"_graphs/social"</span>, 
  * </code><code>    <span class="hljs-string">"name"</span> : <span class="hljs-string">"social"</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def delete(client: HttpClient, graph: String, definition: String, waitForSync: Option[Boolean] = None, dropCollections: Option[Boolean] = None): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/gharial/{graph}/edge/{definition}".withArguments(Map("graph" -> graph, "definition" -> definition)), append = true)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("dropCollections", dropCollections, None)
    .call[Json]

  /**
  * Change one specific edge definition.
  * This will modify all occurrences of this definition in all graphs known to your database.
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
  * Returned if the request was successful and waitForSync is true.
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
  * Returned if the request was successful but waitForSync is false.
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
  * Returned if no edge definition with this name is found in the graph,
  * or of the new definition is ill-formed and cannot be used.
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
  * In order to drop a vertex you at least need to have the following privileges:
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/gharial/social/edge/relation</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"relation"</span>, 
  * </code><code>  <span class="hljs-string">"from"</span> : [ 
  * </code><code>    <span class="hljs-string">"female"</span>, 
  * </code><code>    <span class="hljs-string">"male"</span>, 
  * </code><code>    <span class="hljs-string">"animal"</span> 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"to"</span> : [ 
  * </code><code>    <span class="hljs-string">"female"</span>, 
  * </code><code>    <span class="hljs-string">"male"</span>, 
  * </code><code>    <span class="hljs-string">"animal"</span> 
  * </code><code>  ] 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Accepted
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: _YOn1H1K--B
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
  * </code><code>          <span class="hljs-string">"animal"</span>, 
  * </code><code>          <span class="hljs-string">"female"</span>, 
  * </code><code>          <span class="hljs-string">"male"</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"to"</span> : [ 
  * </code><code>          <span class="hljs-string">"animal"</span>, 
  * </code><code>          <span class="hljs-string">"female"</span>, 
  * </code><code>          <span class="hljs-string">"male"</span> 
  * </code><code>        ] 
  * </code><code>      } 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"orphanCollections"</span> : [ ], 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1H1K--B"</span>, 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"_graphs/social"</span>, 
  * </code><code>    <span class="hljs-string">"name"</span> : <span class="hljs-string">"social"</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def put(client: HttpClient, graph: String, definition: String, waitForSync: Option[Boolean] = None, dropCollections: Option[Boolean] = None, body: GeneralGraphEdgeDefinitionModifyHttpExamples): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/gharial/{graph}/edge/{definition}".withArguments(Map("graph" -> graph, "definition" -> definition)), append = true)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("dropCollections", dropCollections, None)
    .restful[GeneralGraphEdgeDefinitionModifyHttpExamples, Json](body)
}