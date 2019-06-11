package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object AdminStatistics {
  /**
  * Returns the statistics information. The returned object contains the
  * statistics figures grouped together according to the description returned by
  * *_admin/statistics-description*. For instance, to access a figure *userTime*
  * from the group *system*, you first select the sub-object describing the
  * group stored in *system* and in that sub-object the value for *userTime* is
  * stored in the attribute of the same name.
  * 
  * In case of a distribution, the returned object contains the total count in
  * *count* and the distribution list in *counts*. The sum (or total) of the
  * individual values is returned in *sum*.
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * Statistics were returned successfully.
  * 
  * - **code**: the HTTP status code - 200 in this case
  * - **http**:
  *   - **requestsTotal**: total number of http requests
  *   - **requestsPatch**: No of requests using the PATCH-verb
  *   - **requestsPut**: No of requests using the PUT-verb
  *   - **requestsOther**: No of requests using the none of the above identified verbs
  *   - **requestsAsync**: total number of asynchronous http requests
  *   - **requestsPost**: No of requests using the POST-verb
  *   - **requestsOptions**: No of requests using the OPTIONS-verb
  *   - **requestsHead**: No of requests using the HEAD-verb
  *   - **requestsGet**: No of requests using the GET-verb
  *   - **requestsDelete**: No of requests using the DELETE-verb
  * - **errorMessage**: a descriptive error message
  * - **enabled**: *true* if the server has the statistics module enabled. If not, don't expect any values.
  * - **system**:
  *   - **minorPageFaults**: pagefaults
  *   - **majorPageFaults**: pagefaults
  *   - **userTime**: the user CPU time used by the server process
  *   - **systemTime**: the system CPU time used by the server process
  *   - **numberOfThreads**: the number of threads in the server
  *   - **virtualSize**: VSS of the process
  *   - **residentSize**: RSS of process
  *   - **residentSizePercent**: RSS of process in %
  * - **server**:
  *   - **threads**:
  *     - **in-progress**: The number of currently busy worker threads
  *     - **scheduler-threads**: The number of spawned worker threads
  *     - **queued**: The number of jobs queued up waiting for worker threads becomming available
  *   - **uptime**: time the server is up and running
  *   - **physicalMemory**: available physical memory on the server
  *   - **v8Context**:
  *     - **available**: the number of currently spawnen V8 contexts
  *     - **max**: the total number of V8 contexts we may spawn as configured by --javascript.v8-contexts
  *     - **busy**: the number of currently active V8 contexts
  *     - **dirty**: the number of contexts that were previously used, and should now be garbage collected before being re-used
  *     - **free**: the number of V8 contexts that are free to use
  * - **client**:
  *   - **totalTime**:
  *     - **count**: number of values summarized
  *     - **sum**: summarized value of all counts
  *     - **counts** (integer): array containing the values
  *   - **bytesReceived**:
  *     - **count**: number of values summarized
  *     - **sum**: summarized value of all counts
  *     - **counts** (integer): array containing the values
  *   - **requestTime**:
  *     - **count**: number of values summarized
  *     - **sum**: summarized value of all counts
  *     - **counts** (integer): array containing the values
  *   - **connectionTime**:
  *     - **count**: number of values summarized
  *     - **sum**: summarized value of all counts
  *     - **counts** (integer): array containing the values
  *   - **queueTime**:
  *     - **count**: number of values summarized
  *     - **sum**: summarized value of all counts
  *     - **counts** (integer): array containing the values
  *   - **httpConnections**: the number of open http connections
  *   - **bytesSent**:
  *     - **count**: number of values summarized
  *     - **sum**: summarized value of all counts
  *     - **counts** (integer): array containing the values
  *   - **ioTime**:
  *     - **count**: number of values summarized
  *     - **sum**: summarized value of all counts
  *     - **counts** (integer): array containing the values
  * - **error**: boolean flag to indicate whether an error occurred (*false* in this case)
  * - **time**: the current server timestamp
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_admin/statistics</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"time"</span> : <span class="hljs-number">1550658777.0711348</span>, 
  * </code><code>  <span class="hljs-string">"enabled"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"system"</span> : { 
  * </code><code>    <span class="hljs-string">"minorPageFaults"</span> : <span class="hljs-number">82857</span>, 
  * </code><code>    <span class="hljs-string">"majorPageFaults"</span> : <span class="hljs-number">3</span>, 
  * </code><code>    <span class="hljs-string">"userTime"</span> : <span class="hljs-number">3.56</span>, 
  * </code><code>    <span class="hljs-string">"systemTime"</span> : <span class="hljs-number">1.71</span>, 
  * </code><code>    <span class="hljs-string">"numberOfThreads"</span> : <span class="hljs-number">50</span>, 
  * </code><code>    <span class="hljs-string">"residentSize"</span> : <span class="hljs-number">361426944</span>, 
  * </code><code>    <span class="hljs-string">"residentSizePercent"</span> : <span class="hljs-number">0.021646172810734898</span>, 
  * </code><code>    <span class="hljs-string">"virtualSize"</span> : <span class="hljs-number">1353252864</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"client"</span> : { 
  * </code><code>    <span class="hljs-string">"httpConnections"</span> : <span class="hljs-number">1</span>, 
  * </code><code>    <span class="hljs-string">"connectionTime"</span> : { 
  * </code><code>      <span class="hljs-string">"sum"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"counts"</span> : [ 
  * </code><code>        <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-number">0</span> 
  * </code><code>      ] 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"totalTime"</span> : { 
  * </code><code>      <span class="hljs-string">"sum"</span> : <span class="hljs-number">8.05157732963562</span>, 
  * </code><code>      <span class="hljs-string">"count"</span> : <span class="hljs-number">34249</span>, 
  * </code><code>      <span class="hljs-string">"counts"</span> : [ 
  * </code><code>        <span class="hljs-number">34044</span>, 
  * </code><code>        <span class="hljs-number">202</span>, 
  * </code><code>        <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-number">0</span> 
  * </code><code>      ] 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"requestTime"</span> : { 
  * </code><code>      <span class="hljs-string">"sum"</span> : <span class="hljs-number">4.962236642837524</span>, 
  * </code><code>      <span class="hljs-string">"count"</span> : <span class="hljs-number">34249</span>, 
  * </code><code>      <span class="hljs-string">"counts"</span> : [ 
  * </code><code>        <span class="hljs-number">34120</span>, 
  * </code><code>        <span class="hljs-number">126</span>, 
  * </code><code>        <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-number">0</span> 
  * </code><code>      ] 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"queueTime"</span> : { 
  * </code><code>      <span class="hljs-string">"sum"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"count"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"counts"</span> : [ 
  * </code><code>        <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-number">0</span> 
  * </code><code>      ] 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"ioTime"</span> : { 
  * </code><code>      <span class="hljs-string">"sum"</span> : <span class="hljs-number">3.0893406867980957</span>, 
  * </code><code>      <span class="hljs-string">"count"</span> : <span class="hljs-number">34249</span>, 
  * </code><code>      <span class="hljs-string">"counts"</span> : [ 
  * </code><code>        <span class="hljs-number">34173</span>, 
  * </code><code>        <span class="hljs-number">76</span>, 
  * </code><code>        <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-number">0</span> 
  * </code><code>      ] 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"bytesSent"</span> : { 
  * </code><code>      <span class="hljs-string">"sum"</span> : <span class="hljs-number">10108900</span>, 
  * </code><code>      <span class="hljs-string">"count"</span> : <span class="hljs-number">34249</span>, 
  * </code><code>      <span class="hljs-string">"counts"</span> : [ 
  * </code><code>        <span class="hljs-number">240</span>, 
  * </code><code>        <span class="hljs-number">33648</span>, 
  * </code><code>        <span class="hljs-number">332</span>, 
  * </code><code>        <span class="hljs-number">28</span>, 
  * </code><code>        <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-number">0</span> 
  * </code><code>      ] 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"bytesReceived"</span> : { 
  * </code><code>      <span class="hljs-string">"sum"</span> : <span class="hljs-number">8043129</span>, 
  * </code><code>      <span class="hljs-string">"count"</span> : <span class="hljs-number">34249</span>, 
  * </code><code>      <span class="hljs-string">"counts"</span> : [ 
  * </code><code>        <span class="hljs-number">33568</span>, 
  * </code><code>        <span class="hljs-number">681</span>, 
  * </code><code>        <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-number">0</span> 
  * </code><code>      ] 
  * </code><code>    } 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"http"</span> : { 
  * </code><code>    <span class="hljs-string">"requestsTotal"</span> : <span class="hljs-number">34249</span>, 
  * </code><code>    <span class="hljs-string">"requestsAsync"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"requestsGet"</span> : <span class="hljs-number">795</span>, 
  * </code><code>    <span class="hljs-string">"requestsHead"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"requestsPost"</span> : <span class="hljs-number">33323</span>, 
  * </code><code>    <span class="hljs-string">"requestsPut"</span> : <span class="hljs-number">31</span>, 
  * </code><code>    <span class="hljs-string">"requestsPatch"</span> : <span class="hljs-number">2</span>, 
  * </code><code>    <span class="hljs-string">"requestsDelete"</span> : <span class="hljs-number">98</span>, 
  * </code><code>    <span class="hljs-string">"requestsOptions"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"requestsOther"</span> : <span class="hljs-number">0</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"server"</span> : { 
  * </code><code>    <span class="hljs-string">"uptime"</span> : <span class="hljs-number">14.283366203308105</span>, 
  * </code><code>    <span class="hljs-string">"physicalMemory"</span> : <span class="hljs-number">16697036800</span>, 
  * </code><code>    <span class="hljs-string">"v8Context"</span> : { 
  * </code><code>      <span class="hljs-string">"available"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"busy"</span> : <span class="hljs-number">1</span>, 
  * </code><code>      <span class="hljs-string">"dirty"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"free"</span> : <span class="hljs-number">1</span>, 
  * </code><code>      <span class="hljs-string">"max"</span> : <span class="hljs-number">16</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"threads"</span> : { 
  * </code><code>      <span class="hljs-string">"scheduler-threads"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"queued"</span> : <span class="hljs-number">2</span> 
  * </code><code>    } 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  */
  def get(client: HttpClient)(implicit ec: ExecutionContext): Future[GetAdminStatisticsRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_admin/statistics", append = true) 
    .call[GetAdminStatisticsRc200]
}