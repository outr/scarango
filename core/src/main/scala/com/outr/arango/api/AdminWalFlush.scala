package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class AdminWalFlush(client: HttpClient) {
  /**
  * Flushes the write-ahead log. By flushing the currently active write-ahead
  * logfile, the data in it can be transferred to collection journals and
  * datafiles. This is useful to ensure that all data for a collection is
  * present in the collection journals and datafiles, for example, when dumping
  * the data of a collection.
  */
  def put(waitForSync: Option[Boolean] = None, waitForCollector: Option[Boolean] = None): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_admin/wal/flush", append = true) 
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("waitForCollector", waitForCollector, None)
    .call[Json]
}