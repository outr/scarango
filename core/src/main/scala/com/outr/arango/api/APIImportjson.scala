package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APIImportjson(client: HttpClient) {
  /**
  * **NOTE** Swagger examples won't work due to the anchor.
  * 
  * 
  * Creates documents in the collection identified by `collection-name`.
  * The JSON representations of the documents must be passed as the body of the
  * POST request. The request body can either consist of multiple lines, with
  * each line being a single stand-alone JSON object, or a singe JSON array with
  * sub-objects.
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
  *  Importing documents with heterogenous attributes from a JSON array
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/import?collection=products&amp;<span class="hljs-built_in">type</span>=list</span> &lt;&lt;EOF
  * </code><code>[ 
  * </code><code>  { 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"abc"</span>, 
  * </code><code>    <span class="hljs-string">"value1"</span> : <span class="hljs-number">25</span>, 
  * </code><code>    <span class="hljs-string">"value2"</span> : <span class="hljs-string">"test"</span>, 
  * </code><code>    <span class="hljs-string">"allowed"</span> : <span class="hljs-literal">true</span> 
  * </code><code>  }, 
  * </code><code>  { 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"foo"</span>, 
  * </code><code>    <span class="hljs-string">"name"</span> : <span class="hljs-string">"baz"</span> 
  * </code><code>  }, 
  * </code><code>  { 
  * </code><code>    <span class="hljs-string">"name"</span> : { 
  * </code><code>      <span class="hljs-string">"detailed"</span> : <span class="hljs-string">"detailed name"</span>, 
  * </code><code>      <span class="hljs-string">"short"</span> : <span class="hljs-string">"short name"</span> 
  * </code><code>    } 
  * </code><code>  } 
  * </code><code>]
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"created"</span> : <span class="hljs-number">3</span>, 
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
  *  Importing documents from individual JSON lines
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/import?collection=products&amp;<span class="hljs-built_in">type</span>=documents</span> &lt;&lt;EOF
  * </code><code>{ "_key": "abc", "value1": 25, "value2": "test","allowed": true }
  * </code><code>{ "_key": "foo", "name": "baz" }
  * </code><code>
  * </code><code>{ "name": { "detailed": "detailed name", "short": "short name" } }
  * </code><code>
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"created"</span> : <span class="hljs-number">3</span>, 
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
  *  Using the auto type detection
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/import?collection=products&amp;<span class="hljs-built_in">type</span>=auto</span> &lt;&lt;EOF
  * </code><code>[ 
  * </code><code>  { 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"abc"</span>, 
  * </code><code>    <span class="hljs-string">"value1"</span> : <span class="hljs-number">25</span>, 
  * </code><code>    <span class="hljs-string">"value2"</span> : <span class="hljs-string">"test"</span>, 
  * </code><code>    <span class="hljs-string">"allowed"</span> : <span class="hljs-literal">true</span> 
  * </code><code>  }, 
  * </code><code>  { 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"foo"</span>, 
  * </code><code>    <span class="hljs-string">"name"</span> : <span class="hljs-string">"baz"</span> 
  * </code><code>  }, 
  * </code><code>  { 
  * </code><code>    <span class="hljs-string">"name"</span> : { 
  * </code><code>      <span class="hljs-string">"detailed"</span> : <span class="hljs-string">"detailed name"</span>, 
  * </code><code>      <span class="hljs-string">"short"</span> : <span class="hljs-string">"short name"</span> 
  * </code><code>    } 
  * </code><code>  } 
  * </code><code>]
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"created"</span> : <span class="hljs-number">3</span>, 
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
  *  Importing into an edge collection, with attributes `_from`, `_to` and `name`
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/import?collection=links&amp;<span class="hljs-built_in">type</span>=documents</span> &lt;&lt;EOF
  * </code><code>{ "_from": "products/123", "_to": "products/234" }
  * </code><code>{"_from": "products/332", "_to": "products/abc",   "name": "other name" }
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/import?collection=links&amp;<span class="hljs-built_in">type</span>=list&amp;details=<span class="hljs-literal">true</span></span> &lt;&lt;EOF
  * </code><code>[ 
  * </code><code>  { 
  * </code><code>    <span class="hljs-string">"name"</span> : <span class="hljs-string">"some name"</span> 
  * </code><code>  } 
  * </code><code>]
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"created"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"errors"</span> : <span class="hljs-number">1</span>, 
  * </code><code>  <span class="hljs-string">"empty"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"updated"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"ignored"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"details"</span> : [ 
  * </code><code>    <span class="hljs-string">"at position 1: missing '_from' or '_to' attribute, offending document: {\"name\":\"some name\"}"</span> 
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/import?collection=products&amp;<span class="hljs-built_in">type</span>=documents&amp;details=<span class="hljs-literal">true</span></span> &lt;&lt;EOF
  * </code><code>{ "_key": "abc", "value1": 25, "value2": "test" }
  * </code><code>{ "_key": "abc", "value1": "bar", "value2": "baz" }
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/import?collection=products&amp;<span class="hljs-built_in">type</span>=documents&amp;complete=<span class="hljs-literal">true</span></span> &lt;&lt;EOF
  * </code><code>{ "_key": "abc", "value1": 25, "value2": "test" }
  * </code><code>{ "_key": "abc", "value1": "bar", "value2": "baz" }
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/import?collection=products&amp;<span class="hljs-built_in">type</span>=documents</span> &lt;&lt;EOF
  * </code><code>{ "name": "test" }
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/import?collection=products&amp;<span class="hljs-built_in">type</span>=list</span> &lt;&lt;EOF
  * </code><code>{ }
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Bad Request
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"expecting a JSON array in the request"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">400</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">400</span> 
  * </code><code>}
  * </code></pre>
  */
  def post(body: IoCirceJson, `type`: String, collection: String, fromPrefix: Option[String] = None, toPrefix: Option[String] = None, overwrite: Option[Boolean] = None, waitForSync: Option[Boolean] = None, onDuplicate: Option[String] = None, complete: Option[Boolean] = None, details: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Post)
    .path(path"/_db/_system/_api/import#json".withArguments(Map()))
    .params("type" -> type.toString)
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