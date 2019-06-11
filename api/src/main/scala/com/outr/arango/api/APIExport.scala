package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIExport {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **count**: boolean flag that indicates whether the number of documents
  *    in the result set should be returned in the "count" attribute of the result
  *    (optional).
  *    Calculating the "count" attribute might in the future have a performance
  *    impact so this option is turned off by default, and "count" is only returned
  *    when requested.
  *   - **restrict**:
  *     - **fields** (string): Contains an array of attribute names to *include* or *exclude*. Matching of attribute names
  *     for *inclusion* or *exclusion* will be done on the top level only.
  *     Specifying names of nested attributes is not supported at the moment.
  *     - **type**: has to be be set to either *include* or *exclude* depending on which you want to use
  *   - **batchSize**: maximum number of result documents to be transferred from
  *    the server to the client in one roundtrip (optional). If this attribute is
  *    not set, a server-controlled default value will be used.
  *   - **flush**: if set to *true*, a WAL flush operation will be executed prior to the
  *    export. The flush operation will start copying documents from the WAL to the
  *    collection's datafiles. There will be an additional wait time of up
  *    to *flushWait* seconds after the flush to allow the WAL collector to change
  *    the adjusted document meta-data to point into the datafiles, too.
  *    The default value is *false* (i.e. no flush) so most recently inserted or
  *    updated
  *    documents from the collection might be missing in the export.
  *   - **flushWait**: maximum wait time in seconds after a flush operation. The default
  *    value is 10. This option only has an effect when *flush* is set to *true*.
  *   - **limit**: an optional limit value, determining the maximum number of documents to
  *    be included in the cursor. Omitting the *limit* attribute or setting it to 0 will
  *    lead to no limit being used. If a limit is used, it is undefined which documents
  *    from the collection will be included in the export and which will be excluded. 
  *    This is because there is no natural order of documents in a collection.
  *   - **ttl**: an optional time-to-live for the cursor (in seconds). The cursor will be
  *    removed on the server automatically after the specified amount of time. This
  *    is useful to ensure garbage collection of cursors that are not fully fetched
  *    by clients. If not set, a server-defined value will be used.
  * 
  * 
  * 
  * 
  * A call to this method creates a cursor containing all documents in the 
  * specified collection. In contrast to other data-producing APIs, the internal
  * data structures produced by the export API are more lightweight, so it is
  * the preferred way to retrieve all documents from a collection.
  * 
  * Documents are returned in a similar manner as in the `/_api/cursor` REST API. 
  * If all documents of the collection fit into the first batch, then no cursor
  * will be created, and the result object's *hasMore* attribute will be set to
  * *false*. If not all documents fit into the first batch, then the result 
  * object's *hasMore* attribute will be set to *true*, and the *id* attribute
  * of the result will contain a cursor id.
  * 
  * The order in which the documents are returned is not specified.
  * 
  * By default, only those documents from the collection will be returned that are
  * stored in the collection's datafiles. Documents that are present in the write-ahead
  * log (WAL) at the time the export is run will not be exported.
  * 
  * To export these documents as well, the caller can issue a WAL flush request
  * before calling the export API or set the *flush* attribute. Setting the *flush*
  * option will trigger a WAL flush before the export so documents get copied from 
  * the WAL to the collection datafiles.
  * 
  * If the result set can be created by the server, the server will respond with
  * *HTTP 201*. The body of the response will contain a JSON object with the
  * result set.
  * 
  * The returned JSON object has the following properties:
  * 
  * - *error*: boolean flag to indicate that an error occurred (*false*
  *   in this case)
  * 
  * - *code*: the HTTP status code
  * 
  * - *result*: an array of result documents (might be empty if the collection was empty)
  * 
  * - *hasMore*: a boolean indicator whether there are more results
  *   available for the cursor on the server
  * 
  * - *count*: the total number of result documents available (only
  *   available if the query was executed with the *count* attribute set)
  * 
  * - *id*: id of temporary cursor created on the server (optional, see above)
  * 
  * If the JSON representation is malformed or the query specification is
  * missing from the request, the server will respond with *HTTP 400*.
  * 
  * The body of the response will contain a JSON object with additional error
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
  * Clients should always delete an export cursor result as early as possible because a
  * lingering export cursor will prevent the underlying collection from being
  * compacted or unloaded. By default, unused cursors will be deleted automatically 
  * after a server-defined idle time, and clients can adjust this idle time by setting
  * the *ttl* value.
  * 
  * Note: this API is currently not supported on cluster coordinators.
  */
  def post(client: HttpClient, body: PostAPIExport, collection: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/export", append = true) 
    .params("collection" -> collection.toString)
    .restful[PostAPIExport, Json](body)
}