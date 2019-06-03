package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiGharial{Graph}Edge{Collection}{Edge}Put(client: HttpClient) {
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
  def put(graph: String, collection: String, edge: String, waitForSync: Option[Boolean] = None, keepNull: Option[Boolean] = None, returnOld: Option[Boolean] = None, returnNew: Option[Boolean] = None, ifMatch: Option[String] = None, body: GeneralGraphEdgeReplaceHttpExamples): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .params("graph" -> graph.toString)
    .params("collection" -> collection.toString)
    .params("edge" -> edge.toString)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("keepNull", keepNull, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("returnNew", returnNew, None)
    .param[Option[String]]("if-match", if-match, None)
    .restful[GeneralGraphEdgeReplaceHttpExamples, ArangoResponse](body)
}