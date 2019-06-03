package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiGharial{Graph}Edge{Collection}{Edge}Delete(client: HttpClient) {
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
    .params("graph" -> graph.toString)
    .params("collection" -> collection.toString)
    .params("edge" -> edge.toString)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[String]]("if-match", if-match, None)
    .call[GeneralGraphEdgeDeleteHttpExamplesRc200]
}