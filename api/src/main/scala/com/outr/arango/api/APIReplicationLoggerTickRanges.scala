package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
object APIReplicationLoggerTickRanges {
  /**
  * Returns the currently available ranges of tick values for all currently
  * available WAL logfiles. The tick values can be used to determine if certain
  * data (identified by tick value) are still available for replication.
  * 
  * The body of the response contains a JSON array. Each array member is an
  * object
  * that describes a single logfile. Each object has the following attributes:
  * 
  * * *datafile*: name of the logfile
  * 
  * * *status*: status of the datafile, in textual form (e.g. "sealed", "open")
  * 
  * * *tickMin*: minimum tick value contained in logfile
  * 
  * * *tickMax*: maximum tick value contained in logfile
  * 
  * 
  * 
  * 
  * **Example:**
  *  Returns the available tick ranges.
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/replication/logger-tick-ranges</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>[ 
  * </code><code>  { 
  * </code><code>    <span class="hljs-string">"datafile"</span> : <span class="hljs-string">"/tmp/arangosh_uprJb4/tmp-27793-56941049/data/journals/logfile-3.db"</span>, 
  * </code><code>    <span class="hljs-string">"status"</span> : <span class="hljs-string">"collected"</span>, 
  * </code><code>    <span class="hljs-string">"tickMin"</span> : <span class="hljs-string">"5"</span>, 
  * </code><code>    <span class="hljs-string">"tickMax"</span> : <span class="hljs-string">"103215"</span> 
  * </code><code>  }, 
  * </code><code>  { 
  * </code><code>    <span class="hljs-string">"datafile"</span> : <span class="hljs-string">"/tmp/arangosh_uprJb4/tmp-27793-56941049/data/journals/logfile-85.db"</span>, 
  * </code><code>    <span class="hljs-string">"status"</span> : <span class="hljs-string">"collected"</span>, 
  * </code><code>    <span class="hljs-string">"tickMin"</span> : <span class="hljs-string">"103229"</span>, 
  * </code><code>    <span class="hljs-string">"tickMax"</span> : <span class="hljs-string">"103352"</span> 
  * </code><code>  }, 
  * </code><code>  { 
  * </code><code>    <span class="hljs-string">"datafile"</span> : <span class="hljs-string">"/tmp/arangosh_uprJb4/tmp-27793-56941049/data/journals/logfile-232.db"</span>, 
  * </code><code>    <span class="hljs-string">"status"</span> : <span class="hljs-string">"collected"</span>, 
  * </code><code>    <span class="hljs-string">"tickMin"</span> : <span class="hljs-string">"103360"</span>, 
  * </code><code>    <span class="hljs-string">"tickMax"</span> : <span class="hljs-string">"104964"</span> 
  * </code><code>  }, 
  * </code><code>  { 
  * </code><code>    <span class="hljs-string">"datafile"</span> : <span class="hljs-string">"/tmp/arangosh_uprJb4/tmp-27793-56941049/data/journals/logfile-103218.db"</span>, 
  * </code><code>    <span class="hljs-string">"status"</span> : <span class="hljs-string">"collected"</span>, 
  * </code><code>    <span class="hljs-string">"tickMin"</span> : <span class="hljs-string">"104968"</span>, 
  * </code><code>    <span class="hljs-string">"tickMax"</span> : <span class="hljs-string">"104980"</span> 
  * </code><code>  }, 
  * </code><code>  { 
  * </code><code>    <span class="hljs-string">"datafile"</span> : <span class="hljs-string">"/tmp/arangosh_uprJb4/tmp-27793-56941049/data/journals/logfile-103355.db"</span>, 
  * </code><code>    <span class="hljs-string">"status"</span> : <span class="hljs-string">"open"</span>, 
  * </code><code>    <span class="hljs-string">"tickMin"</span> : <span class="hljs-string">"104986"</span>, 
  * </code><code>    <span class="hljs-string">"tickMax"</span> : <span class="hljs-string">"105067"</span> 
  * </code><code>  } 
  * </code><code>]
  * </code></pre>
  */
  def get(client: HttpClient): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/replication/logger-tick-ranges", append = true) 
    .call[Json]
}