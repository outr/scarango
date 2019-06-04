package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APIReplicationApplierConfig(client: HttpClient) {
  /**
  * Returns the configuration of the replication applier.
  * 
  * The body of the response is a JSON object with the configuration. The
  * following attributes may be present in the configuration:
  * 
  * - *endpoint*: the logger server to connect to (e.g. "tcp://192.168.173.13:8529").
  * 
  * - *database*: the name of the database to connect to (e.g. "_system").
  * 
  * - *username*: an optional ArangoDB username to use when connecting to the endpoint.
  * 
  * - *password*: the password to use when connecting to the endpoint.
  * 
  * - *maxConnectRetries*: the maximum number of connection attempts the applier
  *   will make in a row. If the applier cannot establish a connection to the
  *   endpoint in this number of attempts, it will stop itself.
  * 
  * - *connectTimeout*: the timeout (in seconds) when attempting to connect to the
  *   endpoint. This value is used for each connection attempt.
  * 
  * - *requestTimeout*: the timeout (in seconds) for individual requests to the endpoint.
  * 
  * - *chunkSize*: the requested maximum size for log transfer packets that
  *   is used when the endpoint is contacted.
  * 
  * - *autoStart*: whether or not to auto-start the replication applier on
  *   (next and following) server starts
  * 
  * - *adaptivePolling*: whether or not the replication applier will use
  *   adaptive polling.
  * 
  * - *includeSystem*: whether or not system collection operations will be applied
  * 
  * - *autoResync*: whether or not the slave should perform a full automatic
  *   resynchronization with the master in case the master cannot serve log data
  *   requested by the slave, or when the replication is started and no tick
  *   value
  *   can be found.
  * 
  * - *autoResyncRetries*: number of resynchronization retries that will be performed
  *   in a row when automatic resynchronization is enabled and kicks in. Setting this
  *   to *0* will effectively disable *autoResync*. Setting it to some other value
  *   will limit the number of retries that are performed. This helps preventing endless
  *   retries in case resynchronizations always fail.
  * 
  * - *initialSyncMaxWaitTime*: the maximum wait time (in seconds) that the initial
  *   synchronization will wait for a response from the master when fetching initial
  *   collection data.
  *   This wait time can be used to control after what time the initial synchronization
  *   will give up waiting for a response and fail. This value is relevant even
  *   for continuous replication when *autoResync* is set to *true* because this
  *   may re-start the initial synchronization when the master cannot provide
  *   log data the slave requires.
  *   This value will be ignored if set to *0*.
  * 
  * - *connectionRetryWaitTime*: the time (in seconds) that the applier will
  *   intentionally idle before it retries connecting to the master in case of
  *   connection problems.
  *   This value will be ignored if set to *0*.
  * 
  * - *idleMinWaitTime*: the minimum wait time (in seconds) that the applier will
  *   intentionally idle before fetching more log data from the master in case
  *   the master has already sent all its log data. This wait time can be used
  *   to control the frequency with which the replication applier sends HTTP log
  *   fetch requests to the master in case there is no write activity on the master.
  *   This value will be ignored if set to *0*.
  * 
  * - *idleMaxWaitTime*: the maximum wait time (in seconds) that the applier will
  *   intentionally idle before fetching more log data from the master in case the
  *   master has already sent all its log data and there have been previous log
  *   fetch attempts that resulted in no more log data. This wait time can be used
  *   to control the maximum frequency with which the replication applier sends HTTP
  *   log fetch requests to the master in case there is no write activity on the
  *   master for longer periods. This configuration value will only be used if the
  *   option *adaptivePolling* is set to *true*.
  *   This value will be ignored if set to *0*.
  * 
  * - *requireFromPresent*: if set to *true*, then the replication applier will check
  *   at start whether the start tick from which it starts or resumes replication is
  *   still present on the master. If not, then there would be data loss. If
  *   *requireFromPresent* is *true*, the replication applier will abort with an
  *   appropriate error message. If set to *false*, then the replication applier will
  *   still start, and ignore the data loss.
  * 
  * - *verbose*: if set to *true*, then a log line will be emitted for all operations
  *   performed by the replication applier. This should be used for debugging
  *   replication
  *   problems only.
  * 
  * - *restrictType*: the configuration for *restrictCollections*
  * 
  * - *restrictCollections*: the optional array of collections to include or exclude,
  *   based on the setting of *restrictType*
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/replication/applier-config</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"requestTimeout"</span> : <span class="hljs-number">600</span>, 
  * </code><code>  <span class="hljs-string">"connectTimeout"</span> : <span class="hljs-number">10</span>, 
  * </code><code>  <span class="hljs-string">"ignoreErrors"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"maxConnectRetries"</span> : <span class="hljs-number">100</span>, 
  * </code><code>  <span class="hljs-string">"lockTimeoutRetries"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"sslProtocol"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"chunkSize"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"skipCreateDrop"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"autoStart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"adaptivePolling"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"autoResync"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"autoResyncRetries"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"maxPacketSize"</span> : <span class="hljs-number">536870912</span>, 
  * </code><code>  <span class="hljs-string">"includeSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"requireFromPresent"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"verbose"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"incremental"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"restrictType"</span> : <span class="hljs-string">""</span>, 
  * </code><code>  <span class="hljs-string">"restrictCollections"</span> : [ ], 
  * </code><code>  <span class="hljs-string">"connectionRetryWaitTime"</span> : <span class="hljs-number">15</span>, 
  * </code><code>  <span class="hljs-string">"initialSyncMaxWaitTime"</span> : <span class="hljs-number">300</span>, 
  * </code><code>  <span class="hljs-string">"idleMinWaitTime"</span> : <span class="hljs-number">1</span>, 
  * </code><code>  <span class="hljs-string">"idleMaxWaitTime"</span> : <span class="hljs-number">2.5</span>, 
  * </code><code>  <span class="hljs-string">"force32mode"</span> : <span class="hljs-literal">false</span> 
  * </code><code>}
  * </code></pre>
  */
  def get(): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_api/replication/applier-config".withArguments(Map()))
    .call[ArangoResponse]

  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **username**: an optional ArangoDB username to use when connecting to the endpoint.
  *   - **includeSystem**: whether or not system collection operations will be applied
  *   - **endpoint**: the logger server to connect to (e.g. "tcp://192.168.173.13:8529"). The endpoint must be specified.
  *   - **verbose**: if set to *true*, then a log line will be emitted for all operations 
  *    performed by the replication applier. This should be used for debugging replication
  *    problems only.
  *   - **connectTimeout**: the timeout (in seconds) when attempting to connect to the
  *    endpoint. This value is used for each connection attempt.
  *   - **autoResync**: whether or not the slave should perform a full automatic resynchronization
  *    with the master in case the master cannot serve log data requested by the
  *    slave,
  *    or when the replication is started and no tick value can be found.
  *   - **database**: the name of the database on the endpoint. If not specified, defaults to the current local database name.
  *   - **idleMinWaitTime**: the minimum wait time (in seconds) that the applier will intentionally idle
  *    before fetching more log data from the master in case the master has
  *    already sent all its log data. This wait time can be used to control the
  *    frequency with which the replication applier sends HTTP log fetch requests
  *    to the master in case there is no write activity on the master.
  *    This value will be ignored if set to *0*.
  *   - **requestTimeout**: the timeout (in seconds) for individual requests to the endpoint.
  *   - **requireFromPresent**: if set to *true*, then the replication applier will check
  *    at start whether the start tick from which it starts or resumes replication is
  *    still present on the master. If not, then there would be data loss. If 
  *    *requireFromPresent* is *true*, the replication applier will abort with an
  *    appropriate error message. If set to *false*, then the replication applier will
  *    still start, and ignore the data loss.
  *   - **idleMaxWaitTime**: the maximum wait time (in seconds) that the applier will intentionally idle 
  *    before fetching more log data from the master in case the master has 
  *    already sent all its log data and there have been previous log fetch attempts
  *    that resulted in no more log data. This wait time can be used to control the
  *    maximum frequency with which the replication applier sends HTTP log fetch
  *    requests to the master in case there is no write activity on the master for
  *    longer periods. This configuration value will only be used if the option
  *    *adaptivePolling* is set to *true*.
  *    This value will be ignored if set to *0*.
  *   - **restrictCollections** (string): the array of collections to include or exclude,
  *    based on the setting of *restrictType*
  *   - **restrictType**: the configuration for *restrictCollections*; Has to be either *include* or *exclude*
  *   - **initialSyncMaxWaitTime**: the maximum wait time (in seconds) that the initial synchronization will
  *    wait for a response from the master when fetching initial collection data.
  *    This wait time can be used to control after what time the initial
  *    synchronization
  *    will give up waiting for a response and fail. This value is relevant even
  *    for continuous replication when *autoResync* is set to *true* because this
  *    may re-start the initial synchronization when the master cannot provide
  *    log data the slave requires.
  *    This value will be ignored if set to *0*.
  *   - **maxConnectRetries**: the maximum number of connection attempts the applier
  *    will make in a row. If the applier cannot establish a connection to the
  *    endpoint in this number of attempts, it will stop itself.
  *   - **autoStart**: whether or not to auto-start the replication applier on
  *    (next and following) server starts
  *   - **adaptivePolling**: if set to *true*, the replication applier will fall
  *    to sleep for an increasingly long period in case the logger server at the
  *    endpoint does not have any more replication events to apply. Using
  *    adaptive polling is thus useful to reduce the amount of work for both the
  *    applier and the logger server for cases when there are only infrequent
  *    changes. The downside is that when using adaptive polling, it might take
  *    longer for the replication applier to detect that there are new replication
  *    events on the logger server.
  *    Setting *adaptivePolling* to false will make the replication applier
  *    contact the logger server in a constant interval, regardless of whether
  *    the logger server provides updates frequently or seldom.
  *   - **password**: the password to use when connecting to the endpoint.
  *   - **connectionRetryWaitTime**: the time (in seconds) that the applier will intentionally idle before
  *    it retries connecting to the master in case of connection problems.
  *    This value will be ignored if set to *0*.
  *   - **autoResyncRetries**: number of resynchronization retries that will be performed in a row when
  *    automatic resynchronization is enabled and kicks in. Setting this to *0*
  *    will
  *    effectively disable *autoResync*. Setting it to some other value will limit
  *    the number of retries that are performed. This helps preventing endless
  *    retries
  *    in case resynchronizations always fail.
  *   - **chunkSize**: the requested maximum size for log transfer packets that
  *    is used when the endpoint is contacted.
  * 
  * 
  * 
  * 
  * Sets the configuration of the replication applier. The configuration can
  * only be changed while the applier is not running. The updated configuration
  * will be saved immediately but only become active with the next start of the
  * applier.
  * 
  * In case of success, the body of the response is a JSON object with the updated
  * configuration.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X PUT --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/replication/applier-config</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"endpoint"</span> : <span class="hljs-string">"tcp://127.0.0.1:8529"</span>, 
  * </code><code>  <span class="hljs-string">"username"</span> : <span class="hljs-string">"replicationApplier"</span>, 
  * </code><code>  <span class="hljs-string">"password"</span> : <span class="hljs-string">"applier1234@foxx"</span>, 
  * </code><code>  <span class="hljs-string">"chunkSize"</span> : <span class="hljs-number">4194304</span>, 
  * </code><code>  <span class="hljs-string">"autoStart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"adaptivePolling"</span> : <span class="hljs-literal">true</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"endpoint"</span> : <span class="hljs-string">"tcp://127.0.0.1:8529"</span>, 
  * </code><code>  <span class="hljs-string">"database"</span> : <span class="hljs-string">"_system"</span>, 
  * </code><code>  <span class="hljs-string">"username"</span> : <span class="hljs-string">"replicationApplier"</span>, 
  * </code><code>  <span class="hljs-string">"requestTimeout"</span> : <span class="hljs-number">600</span>, 
  * </code><code>  <span class="hljs-string">"connectTimeout"</span> : <span class="hljs-number">10</span>, 
  * </code><code>  <span class="hljs-string">"ignoreErrors"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"maxConnectRetries"</span> : <span class="hljs-number">100</span>, 
  * </code><code>  <span class="hljs-string">"lockTimeoutRetries"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"sslProtocol"</span> : <span class="hljs-number">0</span>, 
  * </code><code>  <span class="hljs-string">"chunkSize"</span> : <span class="hljs-number">4194304</span>, 
  * </code><code>  <span class="hljs-string">"skipCreateDrop"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"autoStart"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"adaptivePolling"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"autoResync"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"autoResyncRetries"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"maxPacketSize"</span> : <span class="hljs-number">536870912</span>, 
  * </code><code>  <span class="hljs-string">"includeSystem"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"requireFromPresent"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"verbose"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"incremental"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"restrictType"</span> : <span class="hljs-string">""</span>, 
  * </code><code>  <span class="hljs-string">"restrictCollections"</span> : [ ], 
  * </code><code>  <span class="hljs-string">"connectionRetryWaitTime"</span> : <span class="hljs-number">15</span>, 
  * </code><code>  <span class="hljs-string">"initialSyncMaxWaitTime"</span> : <span class="hljs-number">300</span>, 
  * </code><code>  <span class="hljs-string">"idleMinWaitTime"</span> : <span class="hljs-number">1</span>, 
  * </code><code>  <span class="hljs-string">"idleMaxWaitTime"</span> : <span class="hljs-number">2.5</span>, 
  * </code><code>  <span class="hljs-string">"force32mode"</span> : <span class="hljs-literal">false</span> 
  * </code><code>}
  * </code></pre>
  */
  def put(body: PutAPIReplicationApplierAdjust): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .path(path"/_db/_system/_api/replication/applier-config".withArguments(Map()))
    .restful[PutAPIReplicationApplierAdjust, ArangoResponse](body)
}