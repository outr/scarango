package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class AdminLogLevel(client: HttpClient) {
  /**
  * Returns the server's current log level settings.
  * The result is a JSON object with the log topics being the object keys, and
  * the log levels being the object values.
  */
  def get(): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_admin/log/level".withArguments(Map()))
    .call[ArangoResponse]

  /**
  * Modifies and returns the server's current log level settings.
  * The request body must be a JSON object with the log topics being the object keys
  * and the log levels being the object values.
  * 
  * The result is a JSON object with the adjusted log topics being the object keys, and
  * the adjusted log levels being the object values.
  * 
  * It can set the log level of all facilities by only specifying the log level as string without json.
  * 
  * Possible log levels are:
  *  - FATAL - There will be no way out of this. ArangoDB will go down after this message.
  *  - ERROR - This is an error. you should investigate and fix it. It may harm your production.
  *  - WARNING - This may be serious application-wise, but we don't know.
  *  - INFO - Something has happened, take notice, but no drama attached.
  *  - DEBUG - output debug messages
  *  - TRACE - trace - prepare your log to be flooded - don't use in production.
  * 
  * 
  * **A JSON object with these properties is required:**
  * 
  *   - **audit-service**: One of the possible log levels.
  *   - **cache**: One of the possible log levels.
  *   - **syscall**: One of the possible log levels.
  *   - **communication**: One of the possible log levels.
  *   - **audit-authentication**: One of the possible log levels.
  *   - **agencycomm**: One of the possible log levels.
  *   - **startup**: One of the possible log levels.
  *   - **audit-authorization**: One of the possible log levels.
  *   - **general**: One of the possible log levels.
  *   - **cluster**: One of the possible log levels.
  *   - **audit-view**: One of the possible log levels.
  *   - **collector**: One of the possible log levels.
  *   - **engines**: One of the possible log levels.
  *   - **trx**: One of the possible log levels.
  *   - **mmap**: One of the possible log levels.
  *   - **agency**: One of the possible log levels.
  *   - **audit-document**: One of the possible log levels.
  *   - **authentication**: One of the possible log levels.
  *   - **memory**: One of the possible log levels.
  *   - **performance**: One of the possible log levels.
  *   - **config**: One of the possible log levels.
  *   - **authorization**: One of the possible log levels.
  *   - **development**: One of the possible log levels.
  *   - **datafiles**: One of the possible log levels.
  *   - **views**: One of the possible log levels.
  *   - **ldap**: One of the possible log levels.
  *   - **replication**: One of the possible log levels.
  *   - **threads**: One of the possible log levels.
  *   - **audit-database**: One of the possible log levels.
  *   - **v8**: One of the possible log levels.
  *   - **ssl**: One of the possible log levels.
  *   - **pregel**: One of the possible log levels.
  *   - **audit-collection**: One of the possible log levels.
  *   - **rocksdb**: One of the possible log levels.
  *   - **supervision**: One of the possible log levels.
  *   - **graphs**: One of the possible log levels.
  *   - **compactor**: One of the possible log levels.
  *   - **queries**: One of the possible log levels.
  *   - **heartbeat**: One of the possible log levels.
  *   - **requests**: One of the possible log levels.
  */
  def put(body: PutAdminLoglevel): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .path(path"/_db/_system/_admin/log/level".withArguments(Map()))
    .restful[PutAdminLoglevel, ArangoResponse](body)
}