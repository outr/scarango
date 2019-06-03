package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiDocument{Collection}Put(client: HttpClient) {
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
    .params("collection" -> collection.toString)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("ignoreRevs", ignoreRevs, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("returnNew", returnNew, None)
    .restful[IoCirceJson, ArangoResponse](body)
}