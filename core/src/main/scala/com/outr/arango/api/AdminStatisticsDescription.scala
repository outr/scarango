package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class AdminStatisticsDescription(client: HttpClient) {
  /**
  * Returns a description of the statistics returned by */_admin/statistics*.
  * The returned objects contains an array of statistics groups in the attribute
  * *groups* and an array of statistics figures in the attribute *figures*.
  * 
  * A statistics group is described by
  * 
  * - *group*: The identifier of the group.
  * - *name*: The name of the group.
  * - *description*: A description of the group.
  * 
  * A statistics figure is described by
  * 
  * - *group*: The identifier of the group to which this figure belongs.
  * - *identifier*: The identifier of the figure. It is unique within the group.
  * - *name*: The name of the figure.
  * - *description*: A description of the figure.
  * - *type*: Either *current*, *accumulated*, or *distribution*.
  * - *cuts*: The distribution vector.
  * - *units*: Units in which the figure is measured.
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * Description was returned successfully.
  * 
  * - **code**: the HTTP status code
  * - **figures**: A statistics figure
  *   - **group**: The identifier of the group to which this figure belongs.
  *   - **name**: The name of the figure.
  *   - **cuts**: The distribution vector.
  *   - **units**: Units in which the figure is measured.
  *   - **identifier**: The identifier of the figure. It is unique within the group.
  *   - **type**: Either *current*, *accumulated*, or *distribution*.
  *   - **description**: A description of the figure.
  * - **groups**: A statistics group
  *   - **group**: The identifier of the group.
  *   - **name**: The name of the group.
  *   - **description**: A description of the group.
  * - **error**: the error, *false* in this case
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_admin/statistics-description</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"groups"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"system"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Process Statistics"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Statistics about the ArangoDB process"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"client"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Client Connection Statistics"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Statistics about the connections."</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"http"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"HTTP Request Statistics"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Statistics about the HTTP requests."</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"server"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Server Statistics"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Statistics about the ArangoDB server"</span> 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"figures"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"system"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"userTime"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"User Time"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Amount of time that this process has been scheduled in user mode, measured in seconds."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"accumulated"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"seconds"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"system"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"systemTime"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"System Time"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Amount of time that this process has been scheduled in kernel mode, measured in seconds."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"accumulated"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"seconds"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"system"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"numberOfThreads"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Number of Threads"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Number of threads in the arangod process."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"current"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"number"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"system"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"residentSize"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Resident Set Size"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"The total size of the number of pages the process has in real memory. This is just the pages which count toward text, data, or stack space. This does not include pages which have not been demand-loaded in, or which are swapped out. The resident set size is reported in bytes."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"current"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"bytes"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"system"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"residentSizePercent"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Resident Set Size"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"The percentage of physical memory used by the process as resident set size."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"current"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"percent"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"system"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"virtualSize"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Virtual Memory Size"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"On Windows, this figure contains the total amount of memory that the memory manager has committed for the arangod process. On other systems, this figure contains The size of the virtual memory the process is using."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"current"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"bytes"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"system"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"minorPageFaults"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Minor Page Faults"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"The number of minor faults the process has made which have not required loading a memory page from disk. This figure is not reported on Windows."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"accumulated"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"number"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"system"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"majorPageFaults"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Major Page Faults"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"On Windows, this figure contains the total number of page faults. On other system, this figure contains the number of major faults the process has made which have required loading a memory page from disk."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"accumulated"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"number"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"client"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"httpConnections"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Client Connections"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"The number of connections that are currently open."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"current"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"number"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"client"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"totalTime"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Total Time"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Total time needed to answer a request."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"distribution"</span>, 
  * </code><code>      <span class="hljs-string">"cuts"</span> : [ 
  * </code><code>        <span class="hljs-number">0.01</span>, 
  * </code><code>        <span class="hljs-number">0.05</span>, 
  * </code><code>        <span class="hljs-number">0.1</span>, 
  * </code><code>        <span class="hljs-number">0.2</span>, 
  * </code><code>        <span class="hljs-number">0.5</span>, 
  * </code><code>        <span class="hljs-number">1</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"seconds"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"client"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"requestTime"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Request Time"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Request time needed to answer a request."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"distribution"</span>, 
  * </code><code>      <span class="hljs-string">"cuts"</span> : [ 
  * </code><code>        <span class="hljs-number">0.01</span>, 
  * </code><code>        <span class="hljs-number">0.05</span>, 
  * </code><code>        <span class="hljs-number">0.1</span>, 
  * </code><code>        <span class="hljs-number">0.2</span>, 
  * </code><code>        <span class="hljs-number">0.5</span>, 
  * </code><code>        <span class="hljs-number">1</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"seconds"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"client"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"queueTime"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Queue Time"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Queue time needed to answer a request."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"distribution"</span>, 
  * </code><code>      <span class="hljs-string">"cuts"</span> : [ 
  * </code><code>        <span class="hljs-number">0.01</span>, 
  * </code><code>        <span class="hljs-number">0.05</span>, 
  * </code><code>        <span class="hljs-number">0.1</span>, 
  * </code><code>        <span class="hljs-number">0.2</span>, 
  * </code><code>        <span class="hljs-number">0.5</span>, 
  * </code><code>        <span class="hljs-number">1</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"seconds"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"client"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"bytesSent"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bytes Sent"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Bytes sents for a request."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"distribution"</span>, 
  * </code><code>      <span class="hljs-string">"cuts"</span> : [ 
  * </code><code>        <span class="hljs-number">250</span>, 
  * </code><code>        <span class="hljs-number">1000</span>, 
  * </code><code>        <span class="hljs-number">2000</span>, 
  * </code><code>        <span class="hljs-number">5000</span>, 
  * </code><code>        <span class="hljs-number">10000</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"bytes"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"client"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"bytesReceived"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bytes Received"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Bytes received for a request."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"distribution"</span>, 
  * </code><code>      <span class="hljs-string">"cuts"</span> : [ 
  * </code><code>        <span class="hljs-number">250</span>, 
  * </code><code>        <span class="hljs-number">1000</span>, 
  * </code><code>        <span class="hljs-number">2000</span>, 
  * </code><code>        <span class="hljs-number">5000</span>, 
  * </code><code>        <span class="hljs-number">10000</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"bytes"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"client"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"connectionTime"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Connection Time"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Total connection time of a client."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"distribution"</span>, 
  * </code><code>      <span class="hljs-string">"cuts"</span> : [ 
  * </code><code>        <span class="hljs-number">0.1</span>, 
  * </code><code>        <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-number">60</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"seconds"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"http"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"requestsTotal"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Total requests"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Total number of HTTP requests."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"accumulated"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"number"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"http"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"requestsAsync"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Async requests"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Number of asynchronously executed HTTP requests."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"accumulated"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"number"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"http"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"requestsGet"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"HTTP GET requests"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Number of HTTP GET requests."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"accumulated"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"number"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"http"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"requestsHead"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"HTTP HEAD requests"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Number of HTTP HEAD requests."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"accumulated"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"number"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"http"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"requestsPost"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"HTTP POST requests"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Number of HTTP POST requests."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"accumulated"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"number"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"http"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"requestsPut"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"HTTP PUT requests"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Number of HTTP PUT requests."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"accumulated"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"number"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"http"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"requestsPatch"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"HTTP PATCH requests"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Number of HTTP PATCH requests."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"accumulated"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"number"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"http"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"requestsDelete"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"HTTP DELETE requests"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Number of HTTP DELETE requests."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"accumulated"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"number"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"http"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"requestsOptions"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"HTTP OPTIONS requests"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Number of HTTP OPTIONS requests."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"accumulated"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"number"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"http"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"requestsOther"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"other HTTP requests"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Number of other HTTP requests."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"accumulated"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"number"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"server"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"uptime"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Server Uptime"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Number of seconds elapsed since server start."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"current"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"seconds"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"group"</span> : <span class="hljs-string">"server"</span>, 
  * </code><code>      <span class="hljs-string">"identifier"</span> : <span class="hljs-string">"physicalMemory"</span>, 
  * </code><code>      <span class="hljs-string">"name"</span> : <span class="hljs-string">"Physical Memory"</span>, 
  * </code><code>      <span class="hljs-string">"description"</span> : <span class="hljs-string">"Physical memory in bytes."</span>, 
  * </code><code>      <span class="hljs-string">"type"</span> : <span class="hljs-string">"current"</span>, 
  * </code><code>      <span class="hljs-string">"units"</span> : <span class="hljs-string">"bytes"</span> 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  */
  def get(): Future[GetAdminStatisticsDescriptionRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_admin/statistics-description".withArguments(Map()))
    .call[GetAdminStatisticsDescriptionRc200]
}