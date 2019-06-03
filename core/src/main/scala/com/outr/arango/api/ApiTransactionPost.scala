package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiTransactionPost(client: HttpClient) {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **maxTransactionSize**: Transaction size limit in bytes. Honored by the RocksDB storage engine only.
  *   - **lockTimeout**: an optional numeric value that can be used to set a
  *    timeout for waiting on collection locks. If not specified, a default
  *    value will be used. Setting *lockTimeout* to *0* will make ArangoDB
  *    not time out waiting for a lock.
  *   - **waitForSync**: an optional boolean flag that, if set, will force the
  *    transaction to write all data to disk before returning.
  *   - **params**: optional arguments passed to *action*.
  *   - **action**: the actual transaction operations to be executed, in the
  *    form of stringified JavaScript code. The code will be executed on server
  *    side, with late binding. It is thus critical that the code specified in
  *    *action* properly sets up all the variables it needs.
  *    If the code specified in *action* ends with a return statement, the
  *    value returned will also be returned by the REST API in the *result*
  *    attribute if the transaction committed successfully.
  *   - **collections**: *collections* must be a JSON object that can have one or all sub-attributes
  *    *read*, *write* or *exclusive*, each being an array of collection names or a
  *    single collection name as string. Collections that will be written to in the
  *    transaction must be declared with the *write* or *exclusive* attribute or it
  *    will fail, whereas non-declared collections from which is solely read will be
  *    added lazily. The optional sub-attribute *allowImplicit* can be set to *false*
  *    to let transactions fail in case of undeclared collections for reading.
  *    Collections for reading should be fully declared if possible, to avoid
  *    deadlocks.
  *    See [locking and isolation](../../Manual/Transactions/LockingAndIsolation.html)
  *    for more information.
  * 
  * 
  * 
  * 
  * The transaction description must be passed in the body of the POST request.
  * 
  * If the transaction is fully executed and committed on the server,
  * *HTTP 200* will be returned. Additionally, the return value of the
  * code defined in *action* will be returned in the *result* attribute.
  * 
  * For successfully committed transactions, the returned JSON object has the
  * following properties:
  * 
  * - *error*: boolean flag to indicate if an error occurred (*false*
  *   in this case)
  * 
  * - *code*: the HTTP status code
  * 
  * - *result*: the return value of the transaction
  * 
  * If the transaction specification is either missing or malformed, the server
  * will respond with *HTTP 400*.
  * 
  * The body of the response will then contain a JSON object with additional error
  * details. The object has the following attributes:
  * 
  * - *error*: boolean flag to indicate that an error occurred (*true* in this case)
  * 
  * - *code*: the HTTP status code
  * 
  * - *errorNum*: the server error number
  * 
  * - *errorMessage*: a descriptive error message
  * 
  * If a transaction fails to commit, either by an exception thrown in the
  * *action* code, or by an internal error, the server will respond with
  * an error.
  * Any other errors will be returned with any of the return codes
  * *HTTP 400*, *HTTP 409*, or *HTTP 500*.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Executing a transaction on a single collection
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/transaction</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collections"</span> : { 
  * </code><code>    <span class="hljs-string">"write"</span> : <span class="hljs-string">"products"</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"action"</span> : <span class="hljs-string">"function () { var db = require('@arangodb').db; db.products.save({});  return db.products.count(); }"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"result"</span> : <span class="hljs-number">1</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Executing a transaction using multiple collections
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/transaction</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collections"</span> : { 
  * </code><code>    <span class="hljs-string">"write"</span> : [ 
  * </code><code>      <span class="hljs-string">"products"</span>, 
  * </code><code>      <span class="hljs-string">"materials"</span> 
  * </code><code>    ] 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"action"</span> : <span class="hljs-string">"function () {var db = require('@arangodb').db;db.products.save({});db.materials.save({});return 'worked!';}"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"result"</span> : <span class="hljs-string">"worked!"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Aborting a transaction due to an internal error
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/transaction</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collections"</span> : { 
  * </code><code>    <span class="hljs-string">"write"</span> : <span class="hljs-string">"products"</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"action"</span> : <span class="hljs-string">"function () {var db = require('@arangodb').db;db.products.save({ _key: 'abc'});db.products.save({ _key: 'abc'});}"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Conflict
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">" - in index 0 of type primary over '_key'; conflicting key: abc"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">409</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1210</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Aborting a transaction by explicitly throwing an exception
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/transaction</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collections"</span> : { 
  * </code><code>    <span class="hljs-string">"read"</span> : <span class="hljs-string">"products"</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"action"</span> : <span class="hljs-string">"function () { throw 'doh!'; }"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Internal Server <span class="hljs-built_in">Error</span>
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"doh!"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">500</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1650</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Referring to a non-existing collection
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/transaction</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"collections"</span> : { 
  * </code><code>    <span class="hljs-string">"read"</span> : <span class="hljs-string">"products"</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"action"</span> : <span class="hljs-string">"function () { return true; }"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Not Found
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"collection or view not found: products"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">404</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1203</span> 
  * </code><code>}
  * </code></pre>
  */
  def post(body: PostAPITransaction): Future[ArangoResponse] = client
    .method(HttpMethod.Post)
    .restful[PostAPITransaction, ArangoResponse](body)
}