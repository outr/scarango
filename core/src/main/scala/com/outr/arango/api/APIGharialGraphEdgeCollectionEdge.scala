package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIGharialGraphEdgeCollectionEdge(client: HttpClient) {
  /**
  * Removes an edge from the collection.
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * Returned if the edge could be removed.
  * 
  * - **removed**: Is set to true if the remove was successful.
  * - **code**: The response code.
  * - **old**:
  *   - **_key**: The _key value of the stored data.
  *   - **_rev**: The _rev value of the stored data.
  *   - **_id**: The _id value of the stored data.
  *   - **_from**: The _from value of the stored data.
  *   - **_to**: The _to value of the stored data.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is false in this response.
  * 
  * 
  * **HTTP 202**
  * *A json document with these Properties is returned:*
  * 
  * Returned if the request was successful but waitForSync is false.
  * 
  * - **removed**: Is set to true if the remove was successful.
  * - **code**: The response code.
  * - **old**:
  *   - **_key**: The _key value of the stored data.
  *   - **_rev**: The _rev value of the stored data.
  *   - **_id**: The _id value of the stored data.
  *   - **_from**: The _from value of the stored data.
  *   - **_to**: The _to value of the stored data.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is false in this response.
  * 
  * 
  * **HTTP 403**
  * *A json document with these Properties is returned:*
  * 
  * Returned if your user has insufficient rights.
  * In order to delete vertices in the graph  you at least need to have the following privileges:
  *   1. `Read Only` access on the Database.
  *   2. `Write` access on the given collection.
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
  * Returned in the following cases:
  * * No graph with this name could be found.
  * * This collection is not part of the graph.
  * * The edge to remove does not exist.
  * 
  * - **errorMessage**: A message created for this error.
  * - **errorNum**: ArangoDB error number for the error that occured.
  * - **code**: The response code.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is true in this response.
  * 
  * 
  * **HTTP 412**
  * *A json document with these Properties is returned:*
  * 
  * Returned if if-match header is given, but the stored documents revision is different.
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/gharial/social/edge/relation/101350</span>
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
  def delete(graph: String, collection: String, edge: String, waitForSync: Option[Boolean] = None, returnOld: Option[Boolean] = None, ifMatch: Option[String] = None): Future[GeneralGraphEdgeDeleteHttpExamplesRc200] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/gharial/{graph}/edge/{collection}/{edge}".withArguments(Map("graph" -> graph, "collection" -> collection, "edge" -> edge)), append = true)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .call[GeneralGraphEdgeDeleteHttpExamplesRc200]

  /**
  * Gets an edge from the given collection.
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * Returned if the edge could be found.
  * 
  * - **edge**:
  *   - **_key**: The _key value of the stored data.
  *   - **_rev**: The _rev value of the stored data.
  *   - **_id**: The _id value of the stored data.
  *   - **_from**: The _from value of the stored data.
  *   - **_to**: The _to value of the stored data.
  * - **code**: The response code.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is false in this response.
  * 
  * 
  * **HTTP 304**
  * *A json document with these Properties is returned:*
  * 
  * Returned if the if-none-match header is given and the
  * currently stored edge still has this revision value.
  * So there was no update between the last time the edge
  * was fetched by the caller.
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
  * In order to update vertices in the graph  you at least need to have the following privileges:
  *   1. `Read Only` access on the Database.
  *   2. `Read Only` access on the given collection.
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
  * Returned in the following cases:
  * * No graph with this name could be found.
  * * This collection is not part of the graph.
  * * The edge does not exist.
  * 
  * - **errorMessage**: A message created for this error.
  * - **errorNum**: ArangoDB error number for the error that occured.
  * - **code**: The response code.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is true in this response.
  * 
  * 
  * **HTTP 412**
  * *A json document with these Properties is returned:*
  * 
  * Returned if if-match header is given, but the stored documents revision is different.
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/gharial/social/edge/relation/101670</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: _YOn1HJG--J
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"edge"</span> : { 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"101670"</span>, 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"relation/101670"</span>, 
  * </code><code>    <span class="hljs-string">"_from"</span> : <span class="hljs-string">"female/alice"</span>, 
  * </code><code>    <span class="hljs-string">"_to"</span> : <span class="hljs-string">"male/charly"</span>, 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1HJG--J"</span>, 
  * </code><code>    <span class="hljs-string">"type"</span> : <span class="hljs-string">"friend"</span>, 
  * </code><code>    <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def get(graph: String, collection: String, edge: String, rev: Option[String] = None, ifMatch: Option[String] = None, ifNoneMatch: Option[String] = None): Future[GeneralGraphEdgeGetHttpExamplesRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_api/gharial/{graph}/edge/{collection}/{edge}".withArguments(Map("graph" -> graph, "collection" -> collection, "edge" -> edge)), append = true)
    .param[Option[String]]("rev", rev, None)
    .call[GeneralGraphEdgeGetHttpExamplesRc200]

  /**
  * Updates the data of the specific edge in the collection.
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * Returned if the edge could be updated, and waitForSync is false.
  * 
  * - **edge**:
  *   - **_key**: The _key value of the stored data.
  *   - **_rev**: The _rev value of the stored data.
  *   - **_id**: The _id value of the stored data.
  *   - **_from**: The _from value of the stored data.
  *   - **_to**: The _to value of the stored data.
  * - **code**: The response code.
  * - **old**:
  *   - **_key**: The _key value of the stored data.
  *   - **_rev**: The _rev value of the stored data.
  *   - **_id**: The _id value of the stored data.
  *   - **_from**: The _from value of the stored data.
  *   - **_to**: The _to value of the stored data.
  * - **new**:
  *   - **_key**: The _key value of the stored data.
  *   - **_rev**: The _rev value of the stored data.
  *   - **_id**: The _id value of the stored data.
  *   - **_from**: The _from value of the stored data.
  *   - **_to**: The _to value of the stored data.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is false in this response.
  * 
  * 
  * **HTTP 202**
  * *A json document with these Properties is returned:*
  * 
  * Returned if the request was successful but waitForSync is false.
  * 
  * - **edge**:
  *   - **_key**: The _key value of the stored data.
  *   - **_rev**: The _rev value of the stored data.
  *   - **_id**: The _id value of the stored data.
  *   - **_from**: The _from value of the stored data.
  *   - **_to**: The _to value of the stored data.
  * - **code**: The response code.
  * - **old**:
  *   - **_key**: The _key value of the stored data.
  *   - **_rev**: The _rev value of the stored data.
  *   - **_id**: The _id value of the stored data.
  *   - **_from**: The _from value of the stored data.
  *   - **_to**: The _to value of the stored data.
  * - **new**:
  *   - **_key**: The _key value of the stored data.
  *   - **_rev**: The _rev value of the stored data.
  *   - **_id**: The _id value of the stored data.
  *   - **_from**: The _from value of the stored data.
  *   - **_to**: The _to value of the stored data.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is false in this response.
  * 
  * 
  * **HTTP 403**
  * *A json document with these Properties is returned:*
  * 
  * Returned if your user has insufficient rights.
  * In order to update edges in the graph  you at least need to have the following privileges:
  *   1. `Read Only` access on the Database.
  *   2. `Write` access on the given collection.
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
  * Returned in the following cases:
  * * No graph with this name could be found.
  * * This collection is not part of the graph.
  * * The edge to update does not exist.
  * * either `_from` or `_to` vertex does not exist (if updated).
  * 
  * - **errorMessage**: A message created for this error.
  * - **errorNum**: ArangoDB error number for the error that occured.
  * - **code**: The response code.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is true in this response.
  * 
  * 
  * **HTTP 412**
  * *A json document with these Properties is returned:*
  * 
  * Returned if if-match header is given, but the stored documents revision is different.
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PATCH --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/gharial/social/edge/relation/102283</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"since"</span> : <span class="hljs-string">"01.01.2001"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Accepted
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: _YOn1HnK--_
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">202</span>, 
  * </code><code>  <span class="hljs-string">"edge"</span> : { 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"relation/102283"</span>, 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"102283"</span>, 
  * </code><code>    <span class="hljs-string">"_oldRev"</span> : <span class="hljs-string">"_YOn1HnG--D"</span>, 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1HnK--_"</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def patch(graph: String, collection: String, edge: String, waitForSync: Option[Boolean] = None, keepNull: Option[Boolean] = None, returnOld: Option[Boolean] = None, returnNew: Option[Boolean] = None, ifMatch: Option[String] = None, body: Json): Future[GeneralGraphEdgeModifyHttpExamplesRc200] = client
    .method(HttpMethod.Patch)
    .path(path"/_api/gharial/{graph}/edge/{collection}/{edge}".withArguments(Map("graph" -> graph, "collection" -> collection, "edge" -> edge)), append = true)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("keepNull", keepNull, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("returnNew", returnNew, None)
    .restful[Json, GeneralGraphEdgeModifyHttpExamplesRc200](body)

  /**
  * Replaces the data of an edge in the collection.
  * 
  * 
  * **A JSON object with these properties is required:**
  * 
  *   - **_from**: The source vertex of this edge. Has to be valid within
  *    the used edge definition.
  *   - **_to**: The target vertex of this edge. Has to be valid within
  *    the used edge definition.
  * 
  * 
  * 
  * **HTTP 201**
  * *A json document with these Properties is returned:*
  * 
  * Returned if the request was successful but waitForSync is true.
  * 
  * - **edge**:
  *   - **_key**: The _key value of the stored data.
  *   - **_rev**: The _rev value of the stored data.
  *   - **_id**: The _id value of the stored data.
  *   - **_from**: The _from value of the stored data.
  *   - **_to**: The _to value of the stored data.
  * - **code**: The response code.
  * - **old**:
  *   - **_key**: The _key value of the stored data.
  *   - **_rev**: The _rev value of the stored data.
  *   - **_id**: The _id value of the stored data.
  *   - **_from**: The _from value of the stored data.
  *   - **_to**: The _to value of the stored data.
  * - **new**:
  *   - **_key**: The _key value of the stored data.
  *   - **_rev**: The _rev value of the stored data.
  *   - **_id**: The _id value of the stored data.
  *   - **_from**: The _from value of the stored data.
  *   - **_to**: The _to value of the stored data.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is false in this response.
  * 
  * 
  * **HTTP 202**
  * *A json document with these Properties is returned:*
  * 
  * Returned if the request was successful but waitForSync is false.
  * 
  * - **edge**:
  *   - **_key**: The _key value of the stored data.
  *   - **_rev**: The _rev value of the stored data.
  *   - **_id**: The _id value of the stored data.
  *   - **_from**: The _from value of the stored data.
  *   - **_to**: The _to value of the stored data.
  * - **code**: The response code.
  * - **old**:
  *   - **_key**: The _key value of the stored data.
  *   - **_rev**: The _rev value of the stored data.
  *   - **_id**: The _id value of the stored data.
  *   - **_from**: The _from value of the stored data.
  *   - **_to**: The _to value of the stored data.
  * - **new**:
  *   - **_key**: The _key value of the stored data.
  *   - **_rev**: The _rev value of the stored data.
  *   - **_id**: The _id value of the stored data.
  *   - **_from**: The _from value of the stored data.
  *   - **_to**: The _to value of the stored data.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is false in this response.
  * 
  * 
  * **HTTP 403**
  * *A json document with these Properties is returned:*
  * 
  * Returned if your user has insufficient rights.
  * In order to replace edges in the graph  you at least need to have the following privileges:
  *   1. `Read Only` access on the Database.
  *   2. `Write` access on the given collection.
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
  * Returned in the following cases:
  * * No graph with this name could be found.
  * * This collection is not part of the graph.
  * * The edge to replace does not exist.
  * * either `_from` or `_to` vertex does not exist.
  * 
  * - **errorMessage**: A message created for this error.
  * - **errorNum**: ArangoDB error number for the error that occured.
  * - **code**: The response code.
  * - **error**: Flag if there was an error (true) or not (false).
  * It is true in this response.
  * 
  * 
  * **HTTP 412**
  * *A json document with these Properties is returned:*
  * 
  * Returned if if-match header is given, but the stored documents revision is different.
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/gharial/social/edge/relation/102360</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"divorced"</span>, 
  * </code><code>  <span class="hljs-string">"_from"</span> : <span class="hljs-string">"female/alice"</span>, 
  * </code><code>  <span class="hljs-string">"_to"</span> : <span class="hljs-string">"male/bob"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Accepted
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: _YOn1HqS--_
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">202</span>, 
  * </code><code>  <span class="hljs-string">"edge"</span> : { 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"relation/102360"</span>, 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"102360"</span>, 
  * </code><code>    <span class="hljs-string">"_oldRev"</span> : <span class="hljs-string">"_YOn1HqO--D"</span>, 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1HqS--_"</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def put(graph: String, collection: String, edge: String, waitForSync: Option[Boolean] = None, keepNull: Option[Boolean] = None, returnOld: Option[Boolean] = None, returnNew: Option[Boolean] = None, ifMatch: Option[String] = None, body: GeneralGraphEdgeReplaceHttpExamples): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/gharial/{graph}/edge/{collection}/{edge}".withArguments(Map("graph" -> graph, "collection" -> collection, "edge" -> edge)), append = true)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("keepNull", keepNull, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("returnNew", returnNew, None)
    .restful[GeneralGraphEdgeReplaceHttpExamples, Json](body)
}