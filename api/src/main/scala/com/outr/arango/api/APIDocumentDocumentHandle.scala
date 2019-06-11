package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIDocumentDocumentHandle {
  /**
  * Replaces the document with handle <document-handle> with the one in
  * the body, provided there is such a document and no precondition is
  * violated.
  * 
  * If the *If-Match* header is specified and the revision of the
  * document in the database is unequal to the given revision, the
  * precondition is violated.
  * 
  * If *If-Match* is not given and *ignoreRevs* is *false* and there
  * is a *_rev* attribute in the body and its value does not match
  * the revision of the document in the database, the precondition is
  * violated.
  * 
  * If a precondition is violated, an *HTTP 412* is returned.
  * 
  * If the document exists and can be updated, then an *HTTP 201* or
  * an *HTTP 202* is returned (depending on *waitForSync*, see below),
  * the *Etag* header field contains the new revision of the document
  * and the *Location* header contains a complete URL under which the
  * document can be queried.
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
  * If *silent* is not set to *true*, the body of the response contains a JSON 
  * object with the information about the handle and the revision. The attribute 
  * *_id* contains the known *document-handle* of the updated document, *_key* 
  * contains the key which uniquely identifies a document in a given collection, 
  * and the attribute *_rev* contains the new document revision.
  * 
  * If the query parameter *returnOld* is *true*, then
  * the complete previous revision of the document
  * is returned under the *old* attribute in the result.
  * 
  * If the query parameter *returnNew* is *true*, then
  * the complete new document is returned under
  * the *new* attribute in the result.
  * 
  * If the document does not exist, then a *HTTP 404* is returned and the
  * body of the response contains an error document.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Using a document handle
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/document/products/104046</span> &lt;&lt;EOF
  * </code><code>{"Hello": "you"}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Accepted
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PRa--D"</span>
  * </code><code>location: <span class="hljs-regexp">/_db/</span>_system/_api/<span class="hljs-built_in">document</span>/products/<span class="hljs-number">104046</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/104046"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"104046"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PRa--D"</span>, 
  * </code><code>  <span class="hljs-string">"_oldRev"</span> : <span class="hljs-string">"_YOn1PRa--B"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Unknown document handle
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/document/products/104082</span> &lt;&lt;EOF
  * </code><code>{}
  * </code><code>EOF
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
  *  Produce a revision conflict
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'If-Match: "_YOn1PSi--B"'</span> --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/document/products/104063</span> &lt;&lt;EOF
  * </code><code>{"other":"content"}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Precondition Failed
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PSi--_"</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">412</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1200</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"precondition failed"</span>, 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/104063"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"104063"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PSi--_"</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(client: HttpClient, body: Json, documentHandle: String, waitForSync: Option[Boolean] = None, ignoreRevs: Option[Boolean] = None, returnOld: Option[Boolean] = None, returnNew: Option[Boolean] = None, silent: Option[Boolean] = None, IfMatch: Option[String] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/document/{document-handle}".withArguments(Map("document-handle" -> documentHandle)), append = true)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("ignoreRevs", ignoreRevs, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("returnNew", returnNew, None)
    .param[Option[Boolean]]("silent", silent, None)
    .restful[Json, Json](body)

  /**
  * Partially updates the document identified by *document-handle*.
  * The body of the request must contain a JSON document with the
  * attributes to patch (the patch document). All attributes from the
  * patch document will be added to the existing document if they do not
  * yet exist, and overwritten in the existing document if they do exist
  * there.
  * 
  * Setting an attribute value to *null* in the patch document will cause a
  * value of *null* to be saved for the attribute by default.
  * 
  * If the *If-Match* header is specified and the revision of the
  * document in the database is unequal to the given revision, the
  * precondition is violated.
  * 
  * If *If-Match* is not given and *ignoreRevs* is *false* and there
  * is a *_rev* attribute in the body and its value does not match
  * the revision of the document in the database, the precondition is
  * violated.
  * 
  * If a precondition is violated, an *HTTP 412* is returned.
  * 
  * If the document exists and can be updated, then an *HTTP 201* or
  * an *HTTP 202* is returned (depending on *waitForSync*, see below),
  * the *Etag* header field contains the new revision of the document
  * (in double quotes) and the *Location* header contains a complete URL
  * under which the document can be queried.
  * 
  * Optionally, the query parameter *waitForSync* can be used to force
  * synchronization of the updated document operation to disk even in case
  * that the *waitForSync* flag had been disabled for the entire collection.
  * Thus, the *waitForSync* query parameter can be used to force synchronization
  * of just specific operations. To use this, set the *waitForSync* parameter
  * to *true*. If the *waitForSync* parameter is not specified or set to
  * *false*, then the collection's default *waitForSync* behavior is
  * applied. The *waitForSync* query parameter cannot be used to disable
  * synchronization for collections that have a default *waitForSync* value
  * of *true*.
  * 
  * If *silent* is not set to *true*, the body of the response contains a JSON 
  * object with the information about the handle and the revision. The attribute 
  * *_id* contains the known *document-handle* of the updated document, *_key* 
  * contains the key which uniquely identifies a document in a given collection, 
  * and the attribute *_rev* contains the new document revision.
  * 
  * If the query parameter *returnOld* is *true*, then
  * the complete previous revision of the document
  * is returned under the *old* attribute in the result.
  * 
  * If the query parameter *returnNew* is *true*, then
  * the complete new document is returned under
  * the *new* attribute in the result.
  * 
  * If the document does not exist, then a *HTTP 404* is returned and the
  * body of the response contains an error document.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Patches an existing document with new content.
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PATCH --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/document/products/103793</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"hello"</span> : <span class="hljs-string">"world"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Accepted
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PAq--_"</span>
  * </code><code>location: <span class="hljs-regexp">/_db/</span>_system/_api/<span class="hljs-built_in">document</span>/products/<span class="hljs-number">103793</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103793"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103793"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PAq--_"</span>, 
  * </code><code>  <span class="hljs-string">"_oldRev"</span> : <span class="hljs-string">"_YOn1PAm--B"</span> 
  * </code><code>}
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PATCH --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/document/products/103793</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"numbers"</span> : { 
  * </code><code>    <span class="hljs-string">"one"</span> : <span class="hljs-number">1</span>, 
  * </code><code>    <span class="hljs-string">"two"</span> : <span class="hljs-number">2</span>, 
  * </code><code>    <span class="hljs-string">"three"</span> : <span class="hljs-number">3</span>, 
  * </code><code>    <span class="hljs-string">"empty"</span> : <span class="hljs-literal">null</span> 
  * </code><code>  } 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Accepted
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PAu--_"</span>
  * </code><code>location: <span class="hljs-regexp">/_db/</span>_system/_api/<span class="hljs-built_in">document</span>/products/<span class="hljs-number">103793</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103793"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103793"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PAu--_"</span>, 
  * </code><code>  <span class="hljs-string">"_oldRev"</span> : <span class="hljs-string">"_YOn1PAq--_"</span> 
  * </code><code>}
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/103793</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PAu--_"</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103793"</span>, 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103793"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PAu--_"</span>, 
  * </code><code>  <span class="hljs-string">"one"</span> : <span class="hljs-string">"world"</span>, 
  * </code><code>  <span class="hljs-string">"hello"</span> : <span class="hljs-string">"world"</span>, 
  * </code><code>  <span class="hljs-string">"numbers"</span> : { 
  * </code><code>    <span class="hljs-string">"one"</span> : <span class="hljs-number">1</span>, 
  * </code><code>    <span class="hljs-string">"two"</span> : <span class="hljs-number">2</span>, 
  * </code><code>    <span class="hljs-string">"three"</span> : <span class="hljs-number">3</span>, 
  * </code><code>    <span class="hljs-string">"empty"</span> : <span class="hljs-literal">null</span> 
  * </code><code>  } 
  * </code><code>}
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PATCH --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/document/products/103793?keepNull=<span class="hljs-literal">false</span></span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"hello"</span> : <span class="hljs-literal">null</span>, 
  * </code><code>  <span class="hljs-string">"numbers"</span> : { 
  * </code><code>    <span class="hljs-string">"four"</span> : <span class="hljs-number">4</span> 
  * </code><code>  } 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Accepted
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PA2--_"</span>
  * </code><code>location: <span class="hljs-regexp">/_db/</span>_system/_api/<span class="hljs-built_in">document</span>/products/<span class="hljs-number">103793</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103793"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103793"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PA2--_"</span>, 
  * </code><code>  <span class="hljs-string">"_oldRev"</span> : <span class="hljs-string">"_YOn1PAu--_"</span> 
  * </code><code>}
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/103793</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PA2--_"</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103793"</span>, 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103793"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PA2--_"</span>, 
  * </code><code>  <span class="hljs-string">"one"</span> : <span class="hljs-string">"world"</span>, 
  * </code><code>  <span class="hljs-string">"numbers"</span> : { 
  * </code><code>    <span class="hljs-string">"empty"</span> : <span class="hljs-literal">null</span>, 
  * </code><code>    <span class="hljs-string">"one"</span> : <span class="hljs-number">1</span>, 
  * </code><code>    <span class="hljs-string">"three"</span> : <span class="hljs-number">3</span>, 
  * </code><code>    <span class="hljs-string">"two"</span> : <span class="hljs-number">2</span>, 
  * </code><code>    <span class="hljs-string">"four"</span> : <span class="hljs-number">4</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Merging attributes of an object using `mergeObjects`:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/103816</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PBq--B"</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103816"</span>, 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103816"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PBq--B"</span>, 
  * </code><code>  <span class="hljs-string">"inhabitants"</span> : { 
  * </code><code>    <span class="hljs-string">"china"</span> : <span class="hljs-number">1366980000</span>, 
  * </code><code>    <span class="hljs-string">"india"</span> : <span class="hljs-number">1263590000</span>, 
  * </code><code>    <span class="hljs-string">"usa"</span> : <span class="hljs-number">319220000</span> 
  * </code><code>  } 
  * </code><code>}
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PATCH --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/document/products/103816?mergeObjects=<span class="hljs-literal">true</span></span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"inhabitants"</span> : { 
  * </code><code>    <span class="hljs-string">"indonesia"</span> : <span class="hljs-number">252164800</span>, 
  * </code><code>    <span class="hljs-string">"brazil"</span> : <span class="hljs-number">203553000</span> 
  * </code><code>  } 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/103816</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PBy--_"</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103816"</span>, 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103816"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PBy--_"</span>, 
  * </code><code>  <span class="hljs-string">"inhabitants"</span> : { 
  * </code><code>    <span class="hljs-string">"china"</span> : <span class="hljs-number">1366980000</span>, 
  * </code><code>    <span class="hljs-string">"india"</span> : <span class="hljs-number">1263590000</span>, 
  * </code><code>    <span class="hljs-string">"usa"</span> : <span class="hljs-number">319220000</span>, 
  * </code><code>    <span class="hljs-string">"indonesia"</span> : <span class="hljs-number">252164800</span>, 
  * </code><code>    <span class="hljs-string">"brazil"</span> : <span class="hljs-number">203553000</span> 
  * </code><code>  } 
  * </code><code>}
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PATCH --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/document/products/103816?mergeObjects=<span class="hljs-literal">false</span></span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"inhabitants"</span> : { 
  * </code><code>    <span class="hljs-string">"pakistan"</span> : <span class="hljs-number">188346000</span> 
  * </code><code>  } 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Accepted
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PB2--_"</span>
  * </code><code>location: <span class="hljs-regexp">/_db/</span>_system/_api/<span class="hljs-built_in">document</span>/products/<span class="hljs-number">103816</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103816"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103816"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PB2--_"</span>, 
  * </code><code>  <span class="hljs-string">"_oldRev"</span> : <span class="hljs-string">"_YOn1PBy--_"</span> 
  * </code><code>}
  * </code><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/103816</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PB2--_"</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103816"</span>, 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103816"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PB2--_"</span>, 
  * </code><code>  <span class="hljs-string">"inhabitants"</span> : { 
  * </code><code>    <span class="hljs-string">"pakistan"</span> : <span class="hljs-number">188346000</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def patch(client: HttpClient, body: Json, documentHandle: String, keepNull: Option[Boolean] = None, mergeObjects: Option[Boolean] = None, waitForSync: Option[Boolean] = None, ignoreRevs: Option[Boolean] = None, returnOld: Option[Boolean] = None, returnNew: Option[Boolean] = None, silent: Option[Boolean] = None, IfMatch: Option[String] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Patch)
    .path(path"/_api/document/{document-handle}".withArguments(Map("document-handle" -> documentHandle)), append = true)
    .param[Option[Boolean]]("keepNull", keepNull, None)
    .param[Option[Boolean]]("mergeObjects", mergeObjects, None)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("ignoreRevs", ignoreRevs, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("returnNew", returnNew, None)
    .param[Option[Boolean]]("silent", silent, None)
    .restful[Json, Json](body)

  /**
  * If *silent* is not set to *true*, the body of the response contains a JSON 
  * object with the information about the handle and the revision. The attribute 
  * *_id* contains the known *document-handle* of the removed document, *_key* 
  * contains the key which uniquely identifies a document in a given collection, 
  * and the attribute *_rev* contains the document revision.
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
  * 
  * 
  * 
  * **Example:**
  *  Using document handle:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/103685</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1O4q--_"</span>
  * </code><code>location: <span class="hljs-regexp">/_db/</span>_system/_api/<span class="hljs-built_in">document</span>/products/<span class="hljs-number">103685</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103685"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103685"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1O4q--_"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Unknown document handle:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/103757</span>
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
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X DELETE --header <span class="hljs-string">'If-Match: "_YOn1O52--D"'</span> --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/103702</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Precondition Failed
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1O52--B"</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">412</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1200</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"precondition failed"</span>, 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103702"</span>, 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103702"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1O52--B"</span> 
  * </code><code>}
  * </code></pre>
  */
  def delete(client: HttpClient, documentHandle: String, waitForSync: Option[Boolean] = None, returnOld: Option[Boolean] = None, silent: Option[Boolean] = None, IfMatch: Option[String] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/document/{document-handle}".withArguments(Map("document-handle" -> documentHandle)), append = true)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("silent", silent, None)
    .call[Json]

  /**
  * Returns the document identified by *document-handle*. The returned
  * document contains three special attributes: *_id* containing the document
  * handle, *_key* containing key which uniquely identifies a document
  * in a given collection and *_rev* containing the revision.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Use a document handle:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/103951</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>etag: <span class="hljs-string">"_YOn1PLW--B"</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103951"</span>, 
  * </code><code>  <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103951"</span>, 
  * </code><code>  <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1PLW--B"</span>, 
  * </code><code>  <span class="hljs-string">"hello"</span> : <span class="hljs-string">"world"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Use a document handle and an Etag:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'If-None-Match: "_YOn1PQK--B"'</span> --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/104028</span>
  * </code><code>
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Unknown document handle:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/unknownhandle</span>
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
  def get(client: HttpClient, documentHandle: String, IfNoneMatch: Option[String] = None, IfMatch: Option[String] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/document/{document-handle}".withArguments(Map("document-handle" -> documentHandle)), append = true)
    .call[Json]

  /**
  * Like *GET*, but only returns the header fields and not the body. You
  * can use this call to get the current revision of a document or check if
  * the document was deleted.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X HEAD --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/document/products/104012</span>
  * </code><code>
  * </code></pre>
  */
  def head(client: HttpClient, documentHandle: String, IfNoneMatch: Option[String] = None, IfMatch: Option[String] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Head)
    .path(path"/_api/document/{document-handle}".withArguments(Map("document-handle" -> documentHandle)), append = true)
    .call[Json]
}