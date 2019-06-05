package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIQuery(client: HttpClient) {
  /**
  * This endpoint is for query validation only. To actually query the database,
  * see `/api/cursor`.
  * 
  * 
  * **A JSON object with these properties is required:**
  * 
  *   - **query**: To validate a query string without executing it, the query string can be
  *    passed to the server via an HTTP POST request.
  * 
  * 
  * 
  * 
  * 
  * **Example:**
  *  a valid query
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/query</span> &lt;&lt;EOF
  * </code><code>{ "query" : "FOR i IN 1..100 FILTER i > 10 LIMIT 2 RETURN i * 3" }
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"parsed"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"collections"</span> : [ ], 
  * </code><code>  <span class="hljs-string">"bindVars"</span> : [ ], 
  * </code><code>  <span class="hljs-string">"ast"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"root"</span>, 
  * </code><code>      <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"for"</span>, 
  * </code><code>          <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"type"</span> : <span class="hljs-string">"variable"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"i"</span>, 
  * </code><code>              <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"type"</span> : <span class="hljs-string">"range"</span>, 
  * </code><code>              <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>                { 
  * </code><code>                  <span class="hljs-string">"type"</span> : <span class="hljs-string">"value"</span>, 
  * </code><code>                  <span class="hljs-string">"value"</span> : <span class="hljs-number">1</span> 
  * </code><code>                }, 
  * </code><code>                { 
  * </code><code>                  <span class="hljs-string">"type"</span> : <span class="hljs-string">"value"</span>, 
  * </code><code>                  <span class="hljs-string">"value"</span> : <span class="hljs-number">100</span> 
  * </code><code>                } 
  * </code><code>              ] 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"type"</span> : <span class="hljs-string">"no-op"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"filter"</span>, 
  * </code><code>          <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"type"</span> : <span class="hljs-string">"compare &gt;"</span>, 
  * </code><code>              <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>                { 
  * </code><code>                  <span class="hljs-string">"type"</span> : <span class="hljs-string">"reference"</span>, 
  * </code><code>                  <span class="hljs-string">"name"</span> : <span class="hljs-string">"i"</span>, 
  * </code><code>                  <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span> 
  * </code><code>                }, 
  * </code><code>                { 
  * </code><code>                  <span class="hljs-string">"type"</span> : <span class="hljs-string">"value"</span>, 
  * </code><code>                  <span class="hljs-string">"value"</span> : <span class="hljs-number">10</span> 
  * </code><code>                } 
  * </code><code>              ] 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"limit"</span>, 
  * </code><code>          <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"type"</span> : <span class="hljs-string">"value"</span>, 
  * </code><code>              <span class="hljs-string">"value"</span> : <span class="hljs-number">0</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"type"</span> : <span class="hljs-string">"value"</span>, 
  * </code><code>              <span class="hljs-string">"value"</span> : <span class="hljs-number">2</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"return"</span>, 
  * </code><code>          <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"type"</span> : <span class="hljs-string">"times"</span>, 
  * </code><code>              <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>                { 
  * </code><code>                  <span class="hljs-string">"type"</span> : <span class="hljs-string">"reference"</span>, 
  * </code><code>                  <span class="hljs-string">"name"</span> : <span class="hljs-string">"i"</span>, 
  * </code><code>                  <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span> 
  * </code><code>                }, 
  * </code><code>                { 
  * </code><code>                  <span class="hljs-string">"type"</span> : <span class="hljs-string">"value"</span>, 
  * </code><code>                  <span class="hljs-string">"value"</span> : <span class="hljs-number">3</span> 
  * </code><code>                } 
  * </code><code>              ] 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        } 
  * </code><code>      ] 
  * </code><code>    } 
  * </code><code>  ] 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  an invalid query
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/query</span> &lt;&lt;EOF
  * </code><code>{ "query" : "FOR i IN 1..100 FILTER i = 1 LIMIT 2 RETURN i * 3" }
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Bad Request
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"syntax error, unexpected assignment near '= 1 LIMIT 2 RETURN i * 3' at position 1:26"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">400</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1501</span> 
  * </code><code>}
  * </code></pre>
  */
  def post(body: PostApiQueryProperties): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/query", append = true) 
    .restful[PostApiQueryProperties, Json](body)
}