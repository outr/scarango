package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APIBatch(client: HttpClient) {
  /**
  * Executes a batch request. A batch request can contain any number of
  * other requests that can be sent to ArangoDB in isolation. The benefit of
  * using batch requests is that batching requests requires less client/server
  * roundtrips than when sending isolated requests.
  * 
  * All parts of a batch request are executed serially on the server. The
  * server will return the results of all parts in a single response when all
  * parts are finished.
  * 
  * Technically, a batch request is a multipart HTTP request, with
  * content-type `multipart/form-data`. A batch request consists of an
  * envelope and the individual batch part actions. Batch part actions
  * are "regular" HTTP requests, including full header and an optional body.
  * Multiple batch parts are separated by a boundary identifier. The
  * boundary identifier is declared in the batch envelope. The MIME content-type
  * for each individual batch part must be `application/x-arango-batchpart`.
  * 
  * Please note that when constructing the individual batch parts, you must
  * use CRLF (`\\\r\\\n`) as the line terminator as in regular HTTP messages.
  * 
  * The response sent by the server will be an `HTTP 200` response, with an
  * optional error summary header `x-arango-errors`. This header contains the
  * number of batch part operations that failed with an HTTP error code of at
  * least 400. This header is only present in the response if the number of
  * errors is greater than zero.
  * 
  * The response sent by the server is a multipart response, too. It contains
  * the individual HTTP responses for all batch parts, including the full HTTP
  * result header (with status code and other potential headers) and an
  * optional result body. The individual batch parts in the result are
  * seperated using the same boundary value as specified in the request.
  * 
  * The order of batch parts in the response will be the same as in the
  * original client request. Client can additionally use the `Content-Id`
  * MIME header in a batch part to define an individual id for each batch part.
  * The server will return this id is the batch part responses, too.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Sending a batch request with five batch parts:
  * 
  * - GET /_api/version
  * 
  * - DELETE /_api/collection/products
  * 
  * - POST /_api/collection/products
  * 
  * - GET /_api/collection/products/figures
  * 
  * - DELETE /_api/collection/products
  * 
  * The boundary (`SomeBoundaryValue`) is passed to the server in the HTTP
  * `Content-Type` HTTP header.
  * *Please note the reply is not displayed all accurate.*
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'Content-Type: multipart/form-data; boundary=SomeBoundaryValue'</span> --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/batch</span> &lt;&lt;EOF
  * </code><code>--SomeBoundaryValue
  * </code><code>Content-Type: application/x-arango-batchpart
  * </code><code>Content-Id: myId1
  * </code><code>
  * </code><code>GET /_api/version HTTP/1.1
  * </code><code>
  * </code><code>--SomeBoundaryValue
  * </code><code>Content-Type: application/x-arango-batchpart
  * </code><code>Content-Id: myId2
  * </code><code>
  * </code><code>DELETE /_api/collection/products HTTP/1.1
  * </code><code>
  * </code><code>--SomeBoundaryValue
  * </code><code>Content-Type: application/x-arango-batchpart
  * </code><code>Content-Id: someId
  * </code><code>
  * </code><code>POST /_api/collection/products HTTP/1.1
  * </code><code>
  * </code><code>{"name": "products" }
  * </code><code>
  * </code><code>--SomeBoundaryValue
  * </code><code>Content-Type: application/x-arango-batchpart
  * </code><code>Content-Id: nextId
  * </code><code>
  * </code><code>GET /_api/collection/products/figures HTTP/1.1
  * </code><code>
  * </code><code>--SomeBoundaryValue
  * </code><code>Content-Type: application/x-arango-batchpart
  * </code><code>Content-Id: otherId
  * </code><code>
  * </code><code>DELETE /_api/collection/products HTTP/1.1
  * </code><code>--SomeBoundaryValue--
  * </code><code>
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/1.1 OK
  * </code><code>content-type: application/json
  * </code><code>x-arango-errors: 1
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>"--SomeBoundaryValue\r\nContent-Type: application/x-arango-batchpart\r\nContent-Id: myId1\r\n\r\nHTTP/1.1 200 OK\r\nServer: \r\nConnection: \r\nContent-Type: application/json; charset=utf-8\r\nContent-Length: 66\r\n\r\n{\"server\":\"arango\",\"version\":\"3.5.0-devel\",\"license\":\"enterprise\"}\r\n--SomeBoundaryValue\r\nContent-Type: application/x-arango-batchpart\r\nContent-Id: myId2\r\n\r\nHTTP/1.1 404 Not Found\r\nServer: \r\nConnection: \r\nContent-Type: application/json; charset=utf-8\r\nContent-Length: 87\r\n\r\n{\"error\":true,\"errorMessage\":\"collection or view not found\",\"code\":404,\"errorNum\":1203}\r\n--SomeBoundaryValue\r\nContent-Type: application/x-arango-batchpart\r\nContent-Id: someId\r\n\r\nHTTP/1.1 200 OK\r\nServer: \r\nConnection: \r\nContent-Type: application/json; charset=utf-8\r\nContent-Length: 328\r\n\r\n{\"error\":false,\"code\":200,\"waitForSync\":false,\"type\":2,\"status\":3,\"journalSize\":33554432,\"keyOptions\":{\"allowUserKeys\":true,\"type\":\"traditional\",\"lastValue\":0},\"globallyUniqueId\":\"h8B2B671BCFD0/102789\",\"statusString\":\"loaded\",\"id\":\"102789\",\"name\":\"products\",\"doCompact\":true,\"isSystem\":false,\"indexBuckets\":8,\"isVolatile\":false}\r\n--SomeBoundaryValue\r\nContent-Type: application/x-arango-batchpart\r\nContent-Id: nextId\r\n\r\nHTTP/1.1 200 OK\r\nServer: \r\nLocation: /_api/collection/products/figures\r\nConnection: \r\nContent-Type: application/json; charset=utf-8\r\nContent-Length: 835\r\n\r\n{\"error\":false,\"code\":200,\"type\":2,\"status\":3,\"journalSize\":33554432,\"isVolatile\":false,\"name\":\"products\",\"doCompact\":true,\"isSystem\":false,\"count\":0,\"waitForSync\":false,\"figures\":{\"indexes\":{\"count\":1,\"size\":32128},\"documentReferences\":0,\"waitingFor\":\"-\",\"alive\":{\"count\":0,\"size\":0},\"dead\":{\"count\":0,\"size\":0,\"deletion\":0},\"compactionStatus\":{\"message\":\"compaction not yet started\",\"time\":\"2019-02-20T10:32:57Z\",\"count\":0,\"filesCombined\":0,\"bytesRead\":0,\"bytesWritten\":0},\"datafiles\":{\"count\":0,\"fileSize\":0},\"journals\":{\"count\":0,\"fileSize\":0},\"compactors\":{\"count\":0,\"fileSize\":0},\"revisions\":{\"count\":0,\"size\":48192},\"lastTick\":0,\"uncollectedLogfileEntries\":0},\"keyOptions\":{\"allowUserKeys\":true,\"type\":\"traditional\",\"lastValue\":0},\"globallyUniqueId\":\"h8B2B671BCFD0/102789\",\"statusString\":\"loaded\",\"id\":\"102789\",\"indexBuckets\":8}\r\n--SomeBoundaryValue\r\nContent-Type: application/x-arango-batchpart\r\nContent-Id: otherId\r\n\r\nHTTP/1.1 200 OK\r\nServer: \r\nConnection: \r\nContent-Type: application/json; charset=utf-8\r\nContent-Length: 40\r\n\r\n{\"error\":false,\"code\":200,\"id\":\"102789\"}\r\n--SomeBoundaryValue--"
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Sending a batch request, setting the boundary implicitly (the server will
  * in this case try to find the boundary at the beginning of the request body).
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/batch</span> &lt;&lt;EOF
  * </code><code>--SomeBoundaryValue
  * </code><code>Content-Type: application/x-arango-batchpart
  * </code><code>
  * </code><code>DELETE /_api/collection/notexisting1 HTTP/1.1
  * </code><code>
  * </code><code>--SomeBoundaryValue
  * </code><code>Content-Type: application/x-arango-batchpart
  * </code><code>
  * </code><code>DELETE _api/collection/notexisting2 HTTP/1.1
  * </code><code>--SomeBoundaryValue--
  * </code><code>
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/1.1 OK
  * </code><code>content-type: application/json
  * </code><code>x-arango-errors: 2
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>"--SomeBoundaryValue\r\nContent-Type: application/x-arango-batchpart\r\n\r\nHTTP/1.1 404 Not Found\r\nServer: \r\nConnection: \r\nContent-Type: application/json; charset=utf-8\r\nContent-Length: 87\r\n\r\n{\"error\":true,\"errorMessage\":\"collection or view not found\",\"code\":404,\"errorNum\":1203}\r\n--SomeBoundaryValue\r\nContent-Type: application/x-arango-batchpart\r\n\r\nHTTP/1.1 404 Not Found\r\nServer: \r\nConnection: \r\nContent-Type: application/json; charset=utf-8\r\nContent-Length: 101\r\n\r\n{\"error\":true,\"code\":404,\"errorNum\":404,\"errorMessage\":\"unknown path '_api/collection/notexisting2'\"}\r\n--SomeBoundaryValue--"
  * </code></pre>
  */
  def post(body: IoCirceJson): Future[ArangoResponse] = client
    .method(HttpMethod.Post)
    .path(path"/_db/_system/_api/batch".withArguments(Map()))
    .restful[IoCirceJson, ArangoResponse](body)
}