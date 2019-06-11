package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIGharialGraphEdgeCollection {
  /**
  * Creates a new edge in the collection.
  * Within the body the edge has to contain a *_from* and *_to* value referencing to valid vertices in the graph.
  * Furthermore the edge has to be valid in the definition of the used 
  * [edge collection](../../Manual/Appendix/Glossary.html#edge-collection).
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
  * Returned if the edge could be created and waitForSync is true.
  * 
  * - **edge**:
  *   - **_key**: The _key value of the stored data.
  *   - **_rev**: The _rev value of the stored data.
  *   - **_id**: The _id value of the stored data.
  *   - **_from**: The _from value of the stored data.
  *   - **_to**: The _to value of the stored data.
  * - **code**: The response code.
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
  * **HTTP 400**
  * *A json document with these Properties is returned:*
  * 
  * Returned if the input document is invalid.
  * This can for instance be the case if `_from` or `_to` is mising.
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
  * In order to insert edges into the graph  you at least need to have the following privileges:
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
  * Returned in any of the following cases:
  * * no graph with this name could be found.
  * * this edge collection is not part of the graph.
  * * either `_from` or `_to` vertex does not exist.
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/gharial/social/edge/relation</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"type"</span> : <span class="hljs-string">"friend"</span>, 
  * </code><code>  <span class="hljs-string">"_from"</span> : <span class="hljs-string">"female/alice"</span>, 
  * </code><code>  <span class="hljs-string">"_to"</span> : <span class="hljs-string">"female/diana"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Accepted
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: _YOn1Gku--_
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">202</span>, 
  * </code><code>  <span class="hljs-string">"edge"</span> : { 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"relation/100926"</span>, 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"100926"</span>, 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Gku--_"</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def post(client: HttpClient, graph: String, collection: String, waitForSync: Option[Boolean] = None, returnNew: Option[Boolean] = None, body: GeneralGraphEdgeCreateHttpExamples)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/gharial/{graph}/edge/{collection}".withArguments(Map("graph" -> graph, "collection" -> collection)), append = true)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("returnNew", returnNew, None)
    .restful[GeneralGraphEdgeCreateHttpExamples, Json](body)
}