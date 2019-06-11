package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIGharial {
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
  def get(client: HttpClient)(implicit ec: ExecutionContext): Future[GeneralGraphListHttpExamplesRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_api/gharial", append = true) 
    .call[GeneralGraphListHttpExamplesRc200]

  /**
  * The creation of a graph requires the name of the graph and a
  * definition of its edges.
  * [See also edge definitions](../../Manual/Graphs/GeneralGraphs/Management.html#edge-definitions).
  * 
  * 
  * **A JSON object with these properties is required:**
  * 
  *   - **isSmart**: Define if the created graph should be smart.
  *    This only has effect in Enterprise Edition.
  *   - **edgeDefinitions**: An array of definitions for the relations of the graph.
  *    Each has the following type:
  *     - **to** (string): List of vertex collection names.
  *     Edges in collection can only be inserted if their _to is in any of the collections here.
  *     - **from** (string): List of vertex collection names.
  *     Edges in collection can only be inserted if their _from is in any of the collections here.
  *     - **collection**: Name of the edge collection, where the edge are stored in.
  *   - **name**: Name of the graph.
  *   - **options**:
  *     - **smartGraphAttribute**: Only has effect in Enterprise Edition and it is required if isSmart is true.
  *     The attribute name that is used to smartly shard the vertices of a graph.
  *     Every vertex in this SmartGraph has to have this attribute.
  *     Cannot be modified later.
  *     - **numberOfShards**: The number of shards that is used for every collection within this graph.
  *     Cannot be modified later.
  *     - **replicationFactor**: The replication factor used when initially creating collections for this graph.
  * 
  * 
  * 
  * **HTTP 201**
  * *A json document with these Properties is returned:*
  * 
  * Is returned if the graph could be created and waitForSync is enabled
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
  * Is returned if the graph could be created and waitForSync is disabled
  * for the `_graphs` collection and not given in the request.
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
  * Returned if the request is in a wrong format.
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
  * In order to create a graph you at least need to have the following privileges:
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
  * **HTTP 409**
  * *A json document with these Properties is returned:*
  * 
  * Returned if there is a conflict storing the graph.  This can occur
  * either if a graph with this name is already stored, or if there is one
  * edge definition with a the same
  * [edge collection](../../Manual/Appendix/Glossary.html#edge-collection) but a
  * different signature used in any other graph.
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/gharial</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"myGraph"</span>, 
  * </code><code>  <span class="hljs-string">"edgeDefinitions"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"collection"</span> : <span class="hljs-string">"edges"</span>, 
  * </code><code>      <span class="hljs-string">"from"</span> : [ 
  * </code><code>        <span class="hljs-string">"startVertices"</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"to"</span> : [ 
  * </code><code>        <span class="hljs-string">"endVertices"</span> 
  * </code><code>      ] 
  * </code><code>    } 
  * </code><code>  ] 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Accepted
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: _YOn1G0O--B
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">202</span>, 
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
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1G0O--B"</span>, 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"_graphs/myGraph"</span>, 
  * </code><code>    <span class="hljs-string">"name"</span> : <span class="hljs-string">"myGraph"</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/gharial</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"myGraph"</span>, 
  * </code><code>  <span class="hljs-string">"edgeDefinitions"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"collection"</span> : <span class="hljs-string">"edges"</span>, 
  * </code><code>      <span class="hljs-string">"from"</span> : [ 
  * </code><code>        <span class="hljs-string">"startVertices"</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"to"</span> : [ 
  * </code><code>        <span class="hljs-string">"endVertices"</span> 
  * </code><code>      ] 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"options"</span> : { 
  * </code><code>    <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">2</span>, 
  * </code><code>    <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">9</span>, 
  * </code><code>    <span class="hljs-string">"smartGraphAttribute"</span> : <span class="hljs-string">"region"</span> 
  * </code><code>  } 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Accepted
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: _YOn1G3a--_
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">202</span>, 
  * </code><code>  <span class="hljs-string">"graph"</span> : { 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"myGraph"</span>, 
  * </code><code>    <span class="hljs-string">"numberOfShards"</span> : <span class="hljs-number">9</span>, 
  * </code><code>    <span class="hljs-string">"replicationFactor"</span> : <span class="hljs-number">2</span>, 
  * </code><code>    <span class="hljs-string">"isSmart"</span> : <span class="hljs-literal">true</span>, 
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
  * </code><code>    <span class="hljs-string">"initial"</span> : <span class="hljs-string">"startVertices"</span>, 
  * </code><code>    <span class="hljs-string">"initialCid"</span> : <span class="hljs-number">101271</span>, 
  * </code><code>    <span class="hljs-string">"smartGraphAttribute"</span> : <span class="hljs-string">"region"</span>, 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1G3a--_"</span>, 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"_graphs/myGraph"</span>, 
  * </code><code>    <span class="hljs-string">"name"</span> : <span class="hljs-string">"myGraph"</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def post(client: HttpClient, waitForSync: Option[Boolean] = None, body: GeneralGraphCreateHttpExamples)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/gharial", append = true) 
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .restful[GeneralGraphCreateHttpExamples, Json](body)
}