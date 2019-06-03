package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiGharial{Graph}Edge{Collection}{Edge}Get(client: HttpClient) {
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
    .params("graph" -> graph.toString)
    .params("collection" -> collection.toString)
    .params("edge" -> edge.toString)
    .param[Option[String]]("rev", rev, None)
    .param[Option[String]]("if-match", if-match, None)
    .param[Option[String]]("if-none-match", if-none-match, None)
    .call[GeneralGraphEdgeGetHttpExamplesRc200]
}