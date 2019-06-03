package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiImport#DocumentPost(client: HttpClient) {
  /**
  * **NOTE** Swagger examples won't work due to the anchor.
  * 
  * 
  * Creates documents in the collection identified by `collection-name`.
  * The first line of the request body must contain a JSON-encoded array of
  * attribute names. All following lines in the request body must contain
  * JSON-encoded arrays of attribute values. Each line is interpreted as a
  * separate document, and the values specified will be mapped to the array
  * of attribute names specified in the first header line.
  * 
  * The response is a JSON object with the following attributes:
  * 
  * - `created`: number of documents imported.
  * 
  * - `errors`: number of documents that were not imported due to an error.
  * 
  * - `empty`: number of empty lines found in the input (will only contain a
  *   value greater zero for types `documents` or `auto`).
  * 
  * - `updated`: number of updated/replaced documents (in case `onDuplicate`
  *   was set to either `update` or `replace`).
  * 
  * - `ignored`: number of failed but ignored insert operations (in case
  *   `onDuplicate` was set to `ignore`).
  * 
  * - `details`: if query parameter `details` is set to true, the result will
  *   contain a `details` attribute which is an array with more detailed
  *   information about which documents could not be inserted.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Importing two documents, with attributes `_key`, `value1` and `value2` each. One
  * line in the import data is empty
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/import?collection=products</span> &lt;&lt;EOF
  * </code><code>[ "_key", "value1", "value2" ]
  * </code><code>[ "abc", 25, "test" ]
  * </code><code>
  * </code><code>[ "foo", "bar", "baz" ]
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"created"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"errors"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"empty"</span> : <span class="hljs-number">1</span>, 
  * </code><code>  <span class="hljs-string">"updated"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"ignored"</span> : <span class="hljs-number">0</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Importing into an edge collection, with attributes `_from`, `_to` and `name`
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/import?collection=links</span> &lt;&lt;EOF
  * </code><code>[ "_from", "_to", "name" ]
  * </code><code>[ "products/123","products/234", "some name" ]
  * </code><code>[ "products/332", "products/abc", "other name" ]
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"created"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"errors"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"empty"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"updated"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"ignored"</span> : <span class="hljs-number">0</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Importing into an edge collection, omitting `_from` or `_to`
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/import?collection=links&amp;details=<span class="hljs-literal">true</span></span> &lt;&lt;EOF
  * </code><code>[ "name" ]
  * </code><code>[ "some name" ]
  * </code><code>[ "other name" ]
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"created"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"errors"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"empty"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"updated"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"ignored"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"details"</span> : [ 
  * </code><code>    <span class="hljs-string">"at position 1: missing '_from' or '_to' attribute, offending document: {\"name\":\"some name\"}"</span>, 
  * </code><code>    <span class="hljs-string">"at position 2: missing '_from' or '_to' attribute, offending document: {\"name\":\"other name\"}"</span> 
  * </code><code>  ] 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Violating a unique constraint, but allow partial imports
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/import?collection=products&amp;details=<span class="hljs-literal">true</span></span> &lt;&lt;EOF
  * </code><code>[ "_key", "value1", "value2" ]
  * </code><code>[ "abc", 25, "test" ]
  * </code><code>["abc", "bar", "baz" ]
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"created"</span> : <span class="hljs-number">1</span>, 
  * </code><code>  <span class="hljs-string">"errors"</span> : <span class="hljs-number">1</span>, 
  * </code><code>  <span class="hljs-string">"empty"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"updated"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"ignored"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"details"</span> : [ 
  * </code><code>    <span class="hljs-string">"at position 1: creating document failed with error 'unique constraint violated', offending document: {\"_key\":\"abc\",\"value1\":\"bar\",\"value2\":\"baz\"}"</span> 
  * </code><code>  ] 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Violating a unique constraint, not allowing partial imports
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/import?collection=products&amp;complete=<span class="hljs-literal">true</span></span> &lt;&lt;EOF
  * </code><code>[ "_key", "value1", "value2" ]
  * </code><code>[ "abc", 25, "test" ]
  * </code><code>["abc", "bar", "baz" ]
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Conflict
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"unique constraint violated"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">409</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1210</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Using a non-existing collection
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/import?collection=products</span> &lt;&lt;EOF
  * </code><code>[ "_key", "value1", "value2" ]
  * </code><code>[ "abc", 25, "test" ]
  * </code><code>["foo", "bar", "baz" ]
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
  * 
  * 
  * 
  * 
  * **Example:**
  *  Using a malformed body
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/import?collection=products</span> &lt;&lt;EOF
  * </code><code>{ "_key": "foo", "value1": "bar" }
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Bad Request
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"no JSON array found in second line"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">400</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">400</span> 
  * </code><code>}
  * </code></pre>
  */
  def post(body: IoCirceJson, collection: String, fromPrefix: Option[String] = None, toPrefix: Option[String] = None, overwrite: Option[Boolean] = None, waitForSync: Option[Boolean] = None, onDuplicate: Option[String] = None, complete: Option[Boolean] = None, details: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Post)
    .params("collection" -> collection.toString)
    .param[Option[String]]("fromPrefix", fromPrefix, None)
    .param[Option[String]]("toPrefix", toPrefix, None)
    .param[Option[Boolean]]("overwrite", overwrite, None)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[String]]("onDuplicate", onDuplicate, None)
    .param[Option[Boolean]]("complete", complete, None)
    .param[Option[Boolean]]("details", details, None)
    .restful[IoCirceJson, ArangoResponse](body)
}