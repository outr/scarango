package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIReplicationApplierState(client: HttpClient) {
  /**
  * Returns the state of the replication applier, regardless of whether the
  * applier is currently running or not.
  * 
  * The response is a JSON object with the following attributes:
  * 
  * - *state*: a JSON object with the following sub-attributes:
  * 
  *   - *running*: whether or not the applier is active and running
  * 
  *   - *lastAppliedContinuousTick*: the last tick value from the continuous
  *     replication log the applier has applied.
  * 
  *   - *lastProcessedContinuousTick*: the last tick value from the continuous
  *     replication log the applier has processed.
  * 
  *     Regularly, the last applied and last processed tick values should be
  *     identical. For transactional operations, the replication applier will first
  *     process incoming log events before applying them, so the processed tick
  *     value might be higher than the applied tick value. This will be the case
  *     until the applier encounters the *transaction commit* log event for the
  *     transaction.
  * 
  *   - *lastAvailableContinuousTick*: the last tick value the remote server can
  *     provide, for all databases.
  * 
  *   - *ticksBehind*: this attribute will be present only if the applier is currently
  *     running. It will provide the number of log ticks between what the applier
  *     has applied/seen and the last log tick value provided by the remote server.
  *     If this value is zero, then both servers are in sync. If this is non-zero,
  *     then the remote server has additional data that the applier has not yet
  *     fetched and processed, or the remote server may have more data that is not
  *     applicable to the applier.
  * 
  *     Client applications can use it to determine approximately how far the applier
  *     is behind the remote server, and can periodically check if the value is 
  *     increasing (applier is falling behind) or decreasing (applier is catching up).
  *     
  *     Please note that as the remote server will only keep one last log tick value 
  *     for all of its databases, but replication may be restricted to just certain 
  *     databases on the applier, this value is more meaningful when the global applier 
  *     is used.
  *     Additionally, the last log tick provided by the remote server may increase
  *     due to writes into system collections that are not replicated due to replication
  *     configuration. So the reported value may exaggerate the reality a bit for
  *     some scenarios. 
  * 
  *   - *time*: the time on the applier server.
  * 
  *   - *totalRequests*: the total number of requests the applier has made to the
  *     endpoint.
  * 
  *   - *totalFailedConnects*: the total number of failed connection attempts the
  *     applier has made.
  * 
  *   - *totalEvents*: the total number of log events the applier has processed.
  * 
  *   - *totalOperationsExcluded*: the total number of log events excluded because
  *     of *restrictCollections*.
  * 
  *   - *progress*: a JSON object with details about the replication applier progress.
  *     It contains the following sub-attributes if there is progress to report:
  * 
  *     - *message*: a textual description of the progress
  * 
  *     - *time*: the date and time the progress was logged
  * 
  *     - *failedConnects*: the current number of failed connection attempts
  * 
  *   - *lastError*: a JSON object with details about the last error that happened on
  *     the applier. It contains the following sub-attributes if there was an error:
  * 
  *     - *errorNum*: a numerical error code
  * 
  *     - *errorMessage*: a textual error description
  * 
  *     - *time*: the date and time the error occurred
  * 
  *     In case no error has occurred, *lastError* will be empty.
  * 
  * - *server*: a JSON object with the following sub-attributes:
  * 
  *   - *version*: the applier server's version
  * 
  *   - *serverId*: the applier server's id
  * 
  * - *endpoint*: the endpoint the applier is connected to (if applier is
  *   active) or will connect to (if applier is currently inactive)
  * 
  * - *database*: the name of the database the applier is connected to (if applier is
  *   active) or will connect to (if applier is currently inactive)
  * 
  * Please note that all "tick" values returned do not have a specific unit. Tick
  * values are only meaningful when compared to each other. Higher tick values mean
  * "later in time" than lower tick values.
  * 
  * 
  * 
  * **Example:**
  *  Fetching the state of an inactive applier:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/replication/applier-state</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"state"</span> : { 
  * </code><code>    <span class="hljs-string">"running"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>    <span class="hljs-string">"phase"</span> : <span class="hljs-string">"inactive"</span>, 
  * </code><code>    <span class="hljs-string">"lastAppliedContinuousTick"</span> : <span class="hljs-literal">null</span>, 
  * </code><code>    <span class="hljs-string">"lastProcessedContinuousTick"</span> : <span class="hljs-literal">null</span>, 
  * </code><code>    <span class="hljs-string">"lastAvailableContinuousTick"</span> : <span class="hljs-literal">null</span>, 
  * </code><code>    <span class="hljs-string">"safeResumeTick"</span> : <span class="hljs-literal">null</span>, 
  * </code><code>    <span class="hljs-string">"progress"</span> : { 
  * </code><code>      <span class="hljs-string">"time"</span> : <span class="hljs-string">"2019-02-20T10:33:03Z"</span>, 
  * </code><code>      <span class="hljs-string">"message"</span> : <span class="hljs-string">"applier shut down"</span>, 
  * </code><code>      <span class="hljs-string">"failedConnects"</span> : <span class="hljs-number">0</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"totalRequests"</span> : <span class="hljs-number">1</span>, 
  * </code><code>    <span class="hljs-string">"totalFailedConnects"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"totalEvents"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"totalResyncs"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"totalOperationsExcluded"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"lastError"</span> : { 
  * </code><code>      <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">0</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"time"</span> : <span class="hljs-string">"2019-02-20T10:33:03Z"</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"server"</span> : { 
  * </code><code>    <span class="hljs-string">"version"</span> : <span class="hljs-string">"3.5.0-devel"</span>, 
  * </code><code>    <span class="hljs-string">"serverId"</span> : <span class="hljs-string">"153018529730512"</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"endpoint"</span> : <span class="hljs-string">"tcp://127.0.0.1:8529"</span>, 
  * </code><code>  <span class="hljs-string">"database"</span> : <span class="hljs-string">"_system"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Fetching the state of an active applier:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/replication/applier-state</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"state"</span> : { 
  * </code><code>    <span class="hljs-string">"running"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"phase"</span> : <span class="hljs-string">"running"</span>, 
  * </code><code>    <span class="hljs-string">"lastAppliedContinuousTick"</span> : <span class="hljs-literal">null</span>, 
  * </code><code>    <span class="hljs-string">"lastProcessedContinuousTick"</span> : <span class="hljs-literal">null</span>, 
  * </code><code>    <span class="hljs-string">"lastAvailableContinuousTick"</span> : <span class="hljs-literal">null</span>, 
  * </code><code>    <span class="hljs-string">"safeResumeTick"</span> : <span class="hljs-literal">null</span>, 
  * </code><code>    <span class="hljs-string">"ticksBehind"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"progress"</span> : { 
  * </code><code>      <span class="hljs-string">"time"</span> : <span class="hljs-string">"2019-02-20T10:33:03Z"</span>, 
  * </code><code>      <span class="hljs-string">"message"</span> : <span class="hljs-string">"fetching master state information"</span>, 
  * </code><code>      <span class="hljs-string">"failedConnects"</span> : <span class="hljs-number">0</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"totalRequests"</span> : <span class="hljs-number">1</span>, 
  * </code><code>    <span class="hljs-string">"totalFailedConnects"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"totalEvents"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"totalResyncs"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"totalOperationsExcluded"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"lastError"</span> : { 
  * </code><code>      <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">0</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"time"</span> : <span class="hljs-string">"2019-02-20T10:33:03Z"</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"server"</span> : { 
  * </code><code>    <span class="hljs-string">"version"</span> : <span class="hljs-string">"3.5.0-devel"</span>, 
  * </code><code>    <span class="hljs-string">"serverId"</span> : <span class="hljs-string">"153018529730512"</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"endpoint"</span> : <span class="hljs-string">"tcp://127.0.0.1:8529"</span>, 
  * </code><code>  <span class="hljs-string">"database"</span> : <span class="hljs-string">"_system"</span> 
  * </code><code>}
  * </code></pre>
  */
  def get(): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_api/replication/applier-state".withArguments(Map()))
    .call[ArangoResponse]
}