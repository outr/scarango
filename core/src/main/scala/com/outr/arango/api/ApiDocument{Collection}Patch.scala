package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiDocument{Collection}Patch(client: HttpClient) {
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
    .params("collection" -> collection.toString)
    .param[Option[Boolean]]("keepNull", keepNull, None)
    .param[Option[Boolean]]("mergeObjects", mergeObjects, None)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("ignoreRevs", ignoreRevs, None)
    .param[Option[Boolean]]("returnOld", returnOld, None)
    .param[Option[Boolean]]("returnNew", returnNew, None)
    .restful[IoCirceJson, ArangoResponse](body)
}