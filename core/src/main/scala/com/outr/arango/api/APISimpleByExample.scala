package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APISimpleByExample(client: HttpClient) {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **skip**: The number of documents to skip in the query (optional).
  *   - **batchSize**: maximum number of result documents to be transferred from
  *    the server to the client in one roundtrip. If this attribute is
  *    not set, a server-controlled default value will be used. A *batchSize* value of
  *    *0* is disallowed.
  *   - **limit**: The maximal amount of documents to return. The *skip*
  *    is applied before the *limit* restriction. (optional)
  *   - **example**: The example document.
  *   - **collection**: The name of the collection to query.
  * 
  * 
  * 
  * 
  * 
  * This will find all documents matching a given example.
  * 
  * Returns a cursor containing the result, see [HTTP Cursor](../AqlQueryCursor/README.md) for details.
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
  * 
  * **Warning:**  
  * Till ArangoDB versions 3.2.13 and 3.3.7 this API is quite expensive.
  * A more lightweight alternative is to use the [HTTP Cursor API](../AqlQueryCursor/README.md).
  * Starting from versions 3.2.14 and 3.3.8 this performance impact is not
  * an issue anymore, as the internal implementation of the API has changed.
  * 
  * 
  * 
  * <!-- Hints End -->
  * 
  * 
  * **Example:**
  *  Matching an attribute
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/simple/by-example</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"example"</span> : { 
  * </code><code>    <span class="hljs-string">"i"</span> : <span class="hljs-number">1</span> 
  * </code><code>  } 
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
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"105192"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/105192"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1XW---_"</span>, 
  * </code><code>      <span class="hljs-string">"a"</span> : { 
  * </code><code>        <span class="hljs-string">"k"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"j"</span> : <span class="hljs-number">1</span> 
  * </code><code>      }, 
  * </code><code>      <span class="hljs-string">"i"</span> : <span class="hljs-number">1</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"105202"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/105202"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1XW---F"</span>, 
  * </code><code>      <span class="hljs-string">"a"</span> : { 
  * </code><code>        <span class="hljs-string">"k"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"j"</span> : <span class="hljs-number">2</span> 
  * </code><code>      }, 
  * </code><code>      <span class="hljs-string">"i"</span> : <span class="hljs-number">1</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"105196"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/105196"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1XW---B"</span>, 
  * </code><code>      <span class="hljs-string">"a"</span> : { 
  * </code><code>        <span class="hljs-string">"j"</span> : <span class="hljs-number">1</span> 
  * </code><code>      }, 
  * </code><code>      <span class="hljs-string">"i"</span> : <span class="hljs-number">1</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"105199"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/105199"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1XW---D"</span>, 
  * </code><code>      <span class="hljs-string">"i"</span> : <span class="hljs-number">1</span> 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"hasMore"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-number">4</span>, 
  * </code><code>  <span class="hljs-string">"cached"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"extra"</span> : { 
  * </code><code>    <span class="hljs-string">"stats"</span> : { 
  * </code><code>      <span class="hljs-string">"writesExecuted"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"writesIgnored"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"scannedFull"</span> : <span class="hljs-number">4</span>, 
  * </code><code>      <span class="hljs-string">"scannedIndex"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"filtered"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"httpRequests"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"executionTime"</span> : <span class="hljs-number">0.0002498626708984375</span>, 
  * </code><code>      <span class="hljs-string">"peakMemoryUsage"</span> : <span class="hljs-number">68336</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"warnings"</span> : [ ] 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Matching an attribute which is a sub-document
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/simple/by-example</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"example"</span> : { 
  * </code><code>    <span class="hljs-string">"a.j"</span> : <span class="hljs-number">1</span> 
  * </code><code>  } 
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
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"105217"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/105217"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1XXC--_"</span>, 
  * </code><code>      <span class="hljs-string">"a"</span> : { 
  * </code><code>        <span class="hljs-string">"k"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"j"</span> : <span class="hljs-number">1</span> 
  * </code><code>      }, 
  * </code><code>      <span class="hljs-string">"i"</span> : <span class="hljs-number">1</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"105221"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/105221"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1XXC--B"</span>, 
  * </code><code>      <span class="hljs-string">"a"</span> : { 
  * </code><code>        <span class="hljs-string">"j"</span> : <span class="hljs-number">1</span> 
  * </code><code>      }, 
  * </code><code>      <span class="hljs-string">"i"</span> : <span class="hljs-number">1</span> 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"hasMore"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"cached"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"extra"</span> : { 
  * </code><code>    <span class="hljs-string">"stats"</span> : { 
  * </code><code>      <span class="hljs-string">"writesExecuted"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"writesIgnored"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"scannedFull"</span> : <span class="hljs-number">4</span>, 
  * </code><code>      <span class="hljs-string">"scannedIndex"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"filtered"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"httpRequests"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"executionTime"</span> : <span class="hljs-number">0.0002300739288330078</span>, 
  * </code><code>      <span class="hljs-string">"peakMemoryUsage"</span> : <span class="hljs-number">68616</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"warnings"</span> : [ ] 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Matching an attribute within a sub-document
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/simple/by-example</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collection"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>  <span class="hljs-string">"example"</span> : { 
  * </code><code>    <span class="hljs-string">"a"</span> : { 
  * </code><code>      <span class="hljs-string">"j"</span> : <span class="hljs-number">1</span> 
  * </code><code>    } 
  * </code><code>  } 
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
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"105246"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/105246"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1XYC--D"</span>, 
  * </code><code>      <span class="hljs-string">"a"</span> : { 
  * </code><code>        <span class="hljs-string">"j"</span> : <span class="hljs-number">1</span> 
  * </code><code>      }, 
  * </code><code>      <span class="hljs-string">"i"</span> : <span class="hljs-number">1</span> 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"hasMore"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-number">1</span>, 
  * </code><code>  <span class="hljs-string">"cached"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"extra"</span> : { 
  * </code><code>    <span class="hljs-string">"stats"</span> : { 
  * </code><code>      <span class="hljs-string">"writesExecuted"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"writesIgnored"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"scannedFull"</span> : <span class="hljs-number">4</span>, 
  * </code><code>      <span class="hljs-string">"scannedIndex"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"filtered"</span> : <span class="hljs-number">3</span>, 
  * </code><code>      <span class="hljs-string">"httpRequests"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"executionTime"</span> : <span class="hljs-number">0.00022554397583007812</span>, 
  * </code><code>      <span class="hljs-string">"peakMemoryUsage"</span> : <span class="hljs-number">68896</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"warnings"</span> : [ ] 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(body: PutAPISimpleByExample): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .path(path"/_db/_system/_api/simple/by-example".withArguments(Map()))
    .restful[PutAPISimpleByExample, ArangoResponse](body)
}