package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
object APIReplicationSync {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **username**: an optional ArangoDB username to use when connecting to the endpoint.
  *   - **includeSystem**: whether or not system collection operations will be applied
  *   - **endpoint**: the master endpoint to connect to (e.g. "tcp://192.168.173.13:8529").
  *   - **initialSyncMaxWaitTime**: the maximum wait time (in seconds) that the initial synchronization will
  *    wait for a response from the master when fetching initial collection data.
  *    This wait time can be used to control after what time the initial synchronization
  *    will give up waiting for a response and fail.
  *    This value will be ignored if set to *0*.
  *   - **database**: the database name on the master (if not specified, defaults to the
  *    name of the local current database).
  *   - **restrictType**: an optional string value for collection filtering. When
  *    specified, the allowed values are *include* or *exclude*.
  *   - **incremental**: if set to *true*, then an incremental synchronization method will be used
  *    for synchronizing data in collections. This method is useful when
  *    collections already exist locally, and only the remaining differences need
  *    to be transferred from the remote endpoint. In this case, the incremental
  *    synchronization can be faster than a full synchronization.
  *    The default value is *false*, meaning that the complete data from the remote
  *    collection will be transferred.
  *   - **restrictCollections** (string): an optional array of collections for use with
  *    *restrictType*. If *restrictType* is *include*, only the specified collections
  *    will be sychronised. If *restrictType* is *exclude*, all but the specified
  *    collections will be synchronized.
  *   - **password**: the password to use when connecting to the endpoint.
  * 
  * 
  * 
  * 
  * Starts a full data synchronization from a remote endpoint into the local
  * ArangoDB database.
  * 
  * The *sync* method can be used by replication clients to connect an ArangoDB database
  * to a remote endpoint, fetch the remote list of collections and indexes, and collection
  * data. It will thus create a local backup of the state of data at the remote ArangoDB
  * database. *sync* works on a per-database level.
  * 
  * *sync* will first fetch the list of collections and indexes from the remote endpoint.
  * It does so by calling the *inventory* API of the remote database. It will then purge
  * data in the local ArangoDB database, and after start will transfer collection data
  * from the remote database to the local ArangoDB database. It will extract data from the
  * remote database by calling the remote database's *dump* API until all data are fetched.
  * 
  * In case of success, the body of the response is a JSON object with the following
  * attributes:
  * 
  * - *collections*: an array of collections that were transferred from the endpoint
  * 
  * - *lastLogTick*: the last log tick on the endpoint at the time the transfer
  *   was started. Use this value as the *from* value when starting the continuous
  *   synchronization later.
  * 
  * WARNING: calling this method will sychronize data from the collections found
  * on the remote endpoint to the local ArangoDB database. All data in the local
  * collections will be purged and replaced with data from the endpoint.
  * 
  * Use with caution!
  * 
  * **Note**: this method is not supported on a coordinator in a cluster.
  */
  def put(client: HttpClient, body: PutAPIReplicationSynchronize): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/replication/sync", append = true) 
    .restful[PutAPIReplicationSynchronize, Json](body)
}