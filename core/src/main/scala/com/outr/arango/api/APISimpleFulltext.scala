package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APISimpleFulltext(client: HttpClient) {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **index**: The identifier of the fulltext-index to use.
  *   - **attribute**: The attribute that contains the texts.
  *   - **collection**: The name of the collection to query.
  *   - **limit**: The maximal amount of documents to return. The *skip*
  *    is applied before the *limit* restriction. (optional)
  *   - **skip**: The number of documents to skip in the query (optional).
  *   - **query**: The fulltext query. Please refer to [Fulltext queries](../../Manual/Appendix/Deprecated/SimpleQueries/FulltextQueries.html)
  *      for details.
  * 
  * 
  * 
  * 
  * 
  * This will find all documents from the collection that match the fulltext
  * query specified in *query*.
  * 
  * In order to use the *fulltext* operator, a fulltext index must be defined
  * for the collection and the specified attribute.
  * 
  * Returns a cursor containing the result, see [HTTP Cursor](../AqlQueryCursor/README.md) for details.
  * 
  * Note: the *fulltext* simple query is **deprecated** as of ArangoDB 2.6. 
  * This API may be removed in future versions of ArangoDB. The preferred
  * way for retrieving documents from a collection using the near operator is
  * to issue an AQL query using the *FULLTEXT* [AQL function](../../AQL/Functions/Fulltext.html) 
  * as follows:
  * 
  *     FOR doc IN FULLTEXT(@@collection, @attributeName, @queryString, @limit) 
  *       RETURN doc
  * 
  * 
  * <!-- Hints Start -->
  * 
  * **Warning:**  
  * This route should no longer be used.
  * All endpoints for Simple Queries are deprecated from version 3.4.0 on.
  * They are superseded by AQL queries.
  * 
  * 
  * 
  * <!-- Hints End -->
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/simple/fulltext</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"attribute"</span> : <span class="hljs-string">"text"</span>, 
  * </code><code>  <span class="hljs-string">"query"</span> : <span class="hljs-string">"word"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"105317"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/105317"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1XbK--_"</span>, 
  * </code><code>      <span class="hljs-string">"text"</span> : <span class="hljs-string">"this text contains word"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"105321"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/105321"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1XbK--B"</span>, 
  * </code><code>      <span class="hljs-string">"text"</span> : <span class="hljs-string">"this text also has a word"</span> 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"hasMore"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(body: PutAPISimpleFulltext): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .path(path"/_db/_system/_api/simple/fulltext".withArguments(Map()))
    .restful[PutAPISimpleFulltext, ArangoResponse](body)
}