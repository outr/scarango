package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class AdminWalFlushPut(client: HttpClient) {
  /**
  * Flushes the write-ahead log. By flushing the currently active write-ahead
  * logfile, the data in it can be transferred to collection journals and
  * datafiles. This is useful to ensure that all data for a collection is
  * present in the collection journals and datafiles, for example, when dumping
  * the data of a collection.
  */
  def put(waitForSync: Option[Boolean] = None, waitForCollector: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("waitForCollector", waitForCollector, None)
    .call[ArangoResponse]
}