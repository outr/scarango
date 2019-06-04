package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APIDocumentCollection(client: HttpClient) {
  /**
  * The body of the request is an array consisting of selectors for
  * documents. A selector can either be a string with a key or a string
  * with a document handle or an object with a *_key* attribute. This
  * API call removes all specified documents from *collection*. If the
  * selector is an object and has a *_rev* attribute, it is a
  * precondition that the actual revision of the removed document in the
  * collection is the specified one.
  * 
  * The body of the response is an array of the same length as the input
  * array. For each input selector, the output contains a JSON object
  * with the information about the outcome of the operation. If no error
  * occurred, an object is built in which the attribute *_id* contains
  * the known *document-handle* of the removed document, *_key* contains
  * the key which uniquely identifies a document in a given collection,
  * and the attribute *_rev* contains the document revision. In case of
  * an error, an object with the attribute *error* set to *true* and
  * *errorCode* set to the error code is built.
  * 
  * If the *waitForSync* parameter is not specified or set to *false*,
  * then the collection's default *waitForSync* behavior is applied.
  * The *waitForSync* query parameter cannot be used to disable
  * synchronization for collections that have a default *waitForSync*
  * value of *true*.
  * 
  * If the query parameter *returnOld* is *true*, then
  * the complete previous revision of the document
  * is returned under the *old* attribute in the result.
  * 
  * Note that if any precondition is violated or an error occurred with
  * some of the documents, the return code is still 200 or 202, but
  * the additional HTTP header *X-Arango-Error-Codes* is set, which
  * contains a map of the error codes that occurred together with their
  * multiplicities, as in: *1200:17,1205:10* which means that in 17
  * cases the error 1200 "revision conflict" and in 10 cases the error
  * 1205 "illegal document handle" has happened.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Using document handle:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/103740</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1O76--B"</span>
  * </code><code>location: <span class="hljs-regexp">/_db/</span>_system/_api/<span class="hljs-built_in">document</span>/products/<span class="hljs-number">103740</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103740"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103740"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1O76--B"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Unknown document handle:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/103775</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Not Found
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"document not found"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">404</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1202</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Revision conflict:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'If-Match: "_YOn1O66--B"'</span> --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/103721</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Precondition Failed
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1O66--_"</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">412</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1200</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"precondition failed"</span>, 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103721"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103721"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1O66--_"</span> 
  * </code><code>}
  * </code></pre>
  */
  def delete(body: IoCirceJson, collection: String, waitForSync: Option[Boolean] = None, returnOld: Option[Boolean] = None, ignoreRevs: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Delete)
    .path(path"/_db/_system/_api/document/{collection}".withArguments(Map("collection" -> collection)))
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("ignoreRevs", ignoreRevs, None)
    .restful[IoCirceJson, ArangoResponse](body)

  /**
  * Partially updates documents, the documents to update are specified
  * by the *_key* attributes in the body objects. The body of the
  * request must contain a JSON array of document updates with the
  * attributes to patch (the patch documents). All attributes from the
  * patch documents will be added to the existing documents if they do
  * not yet exist, and overwritten in the existing documents if they do
  * exist there.
  * 
  * Setting an attribute value to *null* in the patch documents will cause a
  * value of *null* to be saved for the attribute by default.
  * 
  * If *ignoreRevs* is *false* and there is a *_rev* attribute in a
  * document in the body and its value does not match the revision of
  * the corresponding document in the database, the precondition is
  * violated.
  * 
  * If the document exists and can be updated, then an *HTTP 201* or
  * an *HTTP 202* is returned (depending on *waitForSync*, see below).
  * 
  * Optionally, the query parameter *waitForSync* can be used to force
  * synchronization of the document replacement operation to disk even in case
  * that the *waitForSync* flag had been disabled for the entire collection.
  * Thus, the *waitForSync* query parameter can be used to force synchronization
  * of just specific operations. To use this, set the *waitForSync* parameter
  * to *true*. If the *waitForSync* parameter is not specified or set to
  * *false*, then the collection's default *waitForSync* behavior is
  * applied. The *waitForSync* query parameter cannot be used to disable
  * synchronization for collections that have a default *waitForSync* value
  * of *true*.
  * 
  * The body of the response contains a JSON array of the same length
  * as the input array with the information about the handle and the
  * revision of the updated documents. In each entry, the attribute
  * *_id* contains the known *document-handle* of each updated document,
  * *_key* contains the key which uniquely identifies a document in a
  * given collection, and the attribute *_rev* contains the new document
  * revision. In case of an error or violated precondition, an error
  * object with the attribute *error* set to *true* and the attribute
  * *errorCode* set to the error code is built.
  * 
  * If the query parameter *returnOld* is *true*, then, for each
  * generated document, the complete previous revision of the document
  * is returned under the *old* attribute in the result.
  * 
  * If the query parameter *returnNew* is *true*, then, for each
  * generated document, the complete new document is returned under
  * the *new* attribute in the result.
  * 
  * Note that if any precondition is violated or an error occurred with
  * some of the documents, the return code is still 201 or 202, but
  * the additional HTTP header *X-Arango-Error-Codes* is set, which
  * contains a map of the error codes that occurred together with their
  * multiplicities, as in: *1200:17,1205:10* which means that in 17
  * cases the error 1200 "revision conflict" and in 10 cases the error
  * 1205 "illegal document handle" has happened.
  */
  def patch(body: IoCirceJson, collection: String, keepNull: Option[Boolean] = None, mergeObjects: Option[Boolean] = None, waitForSync: Option[Boolean] = None, ignoreRevs: Option[Boolean] = None, returnOld: Option[Boolean] = None, returnNew: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Patch)
    .path(path"/_db/_system/_api/document/{collection}".withArguments(Map("collection" -> collection)))
    .param[Option[Boolean]]("keepNull", keepNull, None)
    .param[Option[Boolean]]("mergeObjects", mergeObjects, None)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("ignoreRevs", ignoreRevs, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("returnNew", returnNew, None)
    .restful[IoCirceJson, ArangoResponse](body)

  /**
  * Creates a new document from the document given in the body, unless there
  * is already a document with the *_key* given. If no *_key* is given, a new
  * unique *_key* is generated automatically.
  * 
  * The body can be an array of documents, in which case all
  * documents in the array are inserted with the same semantics as for a
  * single document. The result body will contain a JSON array of the
  * same length as the input array, and each entry contains the result
  * of the operation for the corresponding input. In case of an error
  * the entry is a document with attributes *error* set to *true* and
  * errorCode set to the error code that has happened.
  * 
  * Possibly given *_id* and *_rev* attributes in the body are always ignored,
  * the URL part or the query parameter collection respectively counts.
  * 
  * If the document was created successfully, then the *Location* header
  * contains the path to the newly created document. The *Etag* header field
  * contains the revision of the document. Both are only set in the single
  * document case.
  * 
  * If *silent* is not set to *true*, the body of the response contains a 
  * JSON object (single document case) with the following attributes:
  * 
  *   - *_id* contains the document handle of the newly created document
  *   - *_key* contains the document key
  *   - *_rev* contains the document revision
  * 
  * In the multi case the body is an array of such objects.
  * 
  * If the collection parameter *waitForSync* is *false*, then the call
  * returns as soon as the document has been accepted. It will not wait
  * until the documents have been synced to disk.
  * 
  * Optionally, the query parameter *waitForSync* can be used to force
  * synchronization of the document creation operation to disk even in
  * case that the *waitForSync* flag had been disabled for the entire
  * collection. Thus, the *waitForSync* query parameter can be used to
  * force synchronization of just this specific operations. To use this,
  * set the *waitForSync* parameter to *true*. If the *waitForSync*
  * parameter is not specified or set to *false*, then the collection's
  * default *waitForSync* behavior is applied. The *waitForSync* query
  * parameter cannot be used to disable synchronization for collections
  * that have a default *waitForSync* value of *true*.
  * 
  * If the query parameter *returnNew* is *true*, then, for each
  * generated document, the complete new document is returned under
  * the *new* attribute in the result.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Create a document in a collection named *products*. Note that the
  * revision identifier might or might not by equal to the auto-generated
  * key.
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/document/products</span> &lt;&lt;EOF
  * </code><code>{ "Hello": "World" }
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PEy--_"</span>
  * </code><code>location: <span class="hljs-regexp">/_db/</span>_system/_api/<span class="hljs-built_in">document</span>/products/<span class="hljs-number">103864</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103864"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103864"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PEy--_"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Create a document in a collection named *products* with a collection-level
  * *waitForSync* value of *false*.
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/document/products</span> &lt;&lt;EOF
  * </code><code>{ "Hello": "World" }
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Accepted
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PCu--_"</span>
  * </code><code>location: <span class="hljs-regexp">/_db/</span>_system/_api/<span class="hljs-built_in">document</span>/products/<span class="hljs-number">103838</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103838"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103838"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PCu--_"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Create a document in a collection with a collection-level *waitForSync*
  * value of *false*, but using the *waitForSync* query parameter.
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/document/products?waitForSync=<span class="hljs-literal">true</span></span> &lt;&lt;EOF
  * </code><code>{ "Hello": "World" }
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PKK--B"</span>
  * </code><code>location: <span class="hljs-regexp">/_db/</span>_system/_api/<span class="hljs-built_in">document</span>/products/<span class="hljs-number">103936</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103936"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103936"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PKK--B"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Unknown collection name
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/document/products</span> &lt;&lt;EOF
  * </code><code>{ "Hello": "World" }
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
  *  Illegal document
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/document/products</span> &lt;&lt;EOF
  * </code><code>{ 1: "World" }
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Bad Request
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"VPackError error: Expecting '\"' or '}'"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">400</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">600</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Insert multiple documents:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/document/products</span> &lt;&lt;EOF
  * </code><code>[{"Hello":"Earth"}, {"Hello":"Venus"}, {"Hello":"Mars"}]
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Accepted
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>[ 
  * </code><code>  { 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103879"</span>, 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103879"</span>, 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PGW--B"</span> 
  * </code><code>  }, 
  * </code><code>  { 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103883"</span>, 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103883"</span>, 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PGW--D"</span> 
  * </code><code>  }, 
  * </code><code>  { 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103885"</span>, 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103885"</span>, 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PGW--F"</span> 
  * </code><code>  } 
  * </code><code>]
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Use of returnNew:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/document/products?returnNew=<span class="hljs-literal">true</span></span> &lt;&lt;EOF
  * </code><code>{"Hello":"World"}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Accepted
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PHa--B"</span>
  * </code><code>location: <span class="hljs-regexp">/_db/</span>_system/_api/<span class="hljs-built_in">document</span>/products/<span class="hljs-number">103900</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103900"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103900"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PHa--B"</span>, 
  * </code><code>  <span class="hljs-string">"new"</span> : { 
  * </code><code>    <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103900"</span>, 
  * </code><code>    <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103900"</span>, 
  * </code><code>    <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PHa--B"</span>, 
  * </code><code>    <span class="hljs-string">"Hello"</span> : <span class="hljs-string">"World"</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/document/products</span> &lt;&lt;EOF
  * </code><code>{ "Hello": "World", "_key" : "lock" }
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PIe--B"</span>
  * </code><code>location: <span class="hljs-regexp">/_db/</span>_system/_api/<span class="hljs-built_in">document</span>/products/lock
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/lock"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"lock"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PIe--B"</span> 
  * </code><code>}
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/document/products?overwrite=<span class="hljs-literal">true</span></span> &lt;&lt;EOF
  * </code><code>{ "Hello": "Universe", "_key" : "lock" }
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PI6--B"</span>
  * </code><code>location: <span class="hljs-regexp">/_db/</span>_system/_api/<span class="hljs-built_in">document</span>/products/lock
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/lock"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"lock"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PI6--B"</span>, 
  * </code><code>  <span class="hljs-string">"_oldRev"</span> : <span class="hljs-string">"_YOn1PIe--B"</span> 
  * </code><code>}
  * </code></pre>
  */
  def post(collection: String, body: IoCirceJson, collection: Option[String] = None, waitForSync: Option[Boolean] = None, returnNew: Option[Boolean] = None, returnOld: Option[Boolean] = None, silent: Option[Boolean] = None, overwrite: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Post)
    .path(path"/_db/_system/_api/document/{collection}".withArguments(Map("collection" -> collection)))
    .param[Option[String]]("collection", collection, None)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("returnNew", returnNew, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("silent", silent, None)
    .param[Option[Boolean]]("overwrite", overwrite, None)
    .restful[IoCirceJson, ArangoResponse](body)

  /**
  * Replaces multiple documents in the specified collection with the
  * ones in the body, the replaced documents are specified by the *_key*
  * attributes in the body documents.
  * 
  * If *ignoreRevs* is *false* and there is a *_rev* attribute in a
  * document in the body and its value does not match the revision of
  * the corresponding document in the database, the precondition is
  * violated.
  * 
  * If the document exists and can be updated, then an *HTTP 201* or
  * an *HTTP 202* is returned (depending on *waitForSync*, see below).
  * 
  * Optionally, the query parameter *waitForSync* can be used to force
  * synchronization of the document replacement operation to disk even in case
  * that the *waitForSync* flag had been disabled for the entire collection.
  * Thus, the *waitForSync* query parameter can be used to force synchronization
  * of just specific operations. To use this, set the *waitForSync* parameter
  * to *true*. If the *waitForSync* parameter is not specified or set to
  * *false*, then the collection's default *waitForSync* behavior is
  * applied. The *waitForSync* query parameter cannot be used to disable
  * synchronization for collections that have a default *waitForSync* value
  * of *true*.
  * 
  * The body of the response contains a JSON array of the same length
  * as the input array with the information about the handle and the
  * revision of the replaced documents. In each entry, the attribute
  * *_id* contains the known *document-handle* of each updated document,
  * *_key* contains the key which uniquely identifies a document in a
  * given collection, and the attribute *_rev* contains the new document
  * revision. In case of an error or violated precondition, an error
  * object with the attribute *error* set to *true* and the attribute
  * *errorCode* set to the error code is built.
  * 
  * If the query parameter *returnOld* is *true*, then, for each
  * generated document, the complete previous revision of the document
  * is returned under the *old* attribute in the result.
  * 
  * If the query parameter *returnNew* is *true*, then, for each
  * generated document, the complete new document is returned under
  * the *new* attribute in the result.
  * 
  * Note that if any precondition is violated or an error occurred with
  * some of the documents, the return code is still 201 or 202, but
  * the additional HTTP header *X-Arango-Error-Codes* is set, which
  * contains a map of the error codes that occurred together with their
  * multiplicities, as in: *1200:17,1205:10* which means that in 17
  * cases the error 1200 "revision conflict" and in 10 cases the error
  * 1205 "illegal document handle" has happened.
  */
  def put(body: IoCirceJson, collection: String, waitForSync: Option[Boolean] = None, ignoreRevs: Option[Boolean] = None, returnOld: Option[Boolean] = None, returnNew: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .path(path"/_db/_system/_api/document/{collection}".withArguments(Map("collection" -> collection)))
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("ignoreRevs", ignoreRevs, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("returnNew", returnNew, None)
    .restful[IoCirceJson, ArangoResponse](body)
}