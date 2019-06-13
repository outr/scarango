package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APICursor {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **count**: indicates whether the number of documents in the result set should be returned in
  *    the "count" attribute of the result.
  *    Calculating the "count" attribute might have a performance impact for some queries
  *    in the future so this option is turned off by default, and "count"
  *    is only returned when requested.
  *   - **batchSize**: maximum number of result documents to be transferred from
  *    the server to the client in one roundtrip. If this attribute is
  *    not set, a server-controlled default value will be used. A *batchSize* value of
  *    *0* is disallowed.
  *   - **cache**: flag to determine whether the AQL query results cache
  *    shall be used. If set to *false*, then any query cache lookup will be skipped
  *    for the query. If set to *true*, it will lead to the query cache being checked
  *    for the query if the query cache mode is either *on* or *demand*.
  *   - **memoryLimit**: the maximum number of memory (measured in bytes) that the query is allowed to
  *    use. If set, then the query will fail with error "resource limit exceeded" in
  *    case it allocates too much memory. A value of *0* indicates that there is no
  *    memory limit.
  *   - **ttl**: The time-to-live for the cursor (in seconds). The cursor will be
  *    removed on the server automatically after the specified amount of time. This
  *    is useful to ensure garbage collection of cursors that are not fully fetched
  *    by clients. If not set, a server-defined value will be used (default: 30 seconds).
  *   - **query**: contains the query string to be executed
  *   - **bindVars** (object): key/value pairs representing the bind parameters.
  *   - **options**:
  *     - **failOnWarning**: When set to *true*, the query will throw an exception and abort instead of producing
  *     a warning. This option should be used during development to catch potential issues
  *     early. When the attribute is set to *false*, warnings will not be propagated to
  *     exceptions and will be returned with the query result.
  *     There is also a server configuration option `--query.fail-on-warning` for setting the
  *     default value for *failOnWarning* so it does not need to be set on a per-query level.
  *     - **profile**: If set to *true* or *1*, then the additional query profiling information will be returned
  *     in the sub-attribute *profile* of the *extra* return attribute, if the query result
  *     is not served from the query cache. Set to *2* the query will include execution stats
  *     per query plan node in sub-attribute *stats.nodes* of the *extra* return attribute.
  *     Additionally the query plan is returned in the sub-attribute *extra.plan*.
  *     - **maxTransactionSize**: Transaction size limit in bytes. Honored by the RocksDB storage engine only.
  *     - **stream**: Specify *true* and the query will be executed in a **streaming** fashion. The query result is
  *     not stored on the server, but calculated on the fly. *Beware*: long-running queries will
  *     need to hold the collection locks for as long as the query cursor exists. 
  *     When set to *false* a query will be executed right away in its entirety. 
  *     In that case query results are either returned right away (if the result set is small enough),
  *     or stored on the arangod instance and accessible via the cursor API (with respect to the `ttl`). 
  *     It is advisable to *only* use this option on short-running queries or without exclusive locks 
  *     (write-locks on MMFiles).
  *     Please note that the query options `cache`, `count` and `fullCount` will not work on streaming queries.
  *     Additionally query statistics, warnings and profiling data will only be available after the query is finished.
  *     The default value is *false*
  *     - **skipInaccessibleCollections**: AQL queries (especially graph traversals) will treat collection to which a user has no access rights as if these collections were empty. Instead of returning a forbidden access error, your queries will execute normally. This is intended to help with certain use-cases: A graph contains several collections and different users execute AQL queries on that graph. You can now naturally limit the accessible results by changing the access rights of users on collections. This feature is only available in the Enterprise Edition.
  *     - **maxWarningCount**: Limits the maximum number of warnings a query will return. The number of warnings
  *     a query will return is limited to 10 by default, but that number can be increased
  *     or decreased by setting this attribute.
  *     - **intermediateCommitCount**: Maximum number of operations after which an intermediate commit is performed
  *     automatically. Honored by the RocksDB storage engine only.
  *     - **satelliteSyncWait**: This *Enterprise Edition* parameter allows to configure how long a DBServer will have time
  *     to bring the satellite collections involved in the query into sync.
  *     The default value is *60.0* (seconds). When the max time has been reached the query
  *     will be stopped.
  *     - **fullCount**: if set to *true* and the query contains a *LIMIT* clause, then the
  *     result will have an *extra* attribute with the sub-attributes *stats*
  *     and *fullCount*, `{ ... , "extra": { "stats": { "fullCount": 123 } } }`.
  *     The *fullCount* attribute will contain the number of documents in the result before the
  *     last top-level LIMIT in the query was applied. It can be used to count the number of 
  *     documents that match certain filter criteria, but only return a subset of them, in one go.
  *     It is thus similar to MySQL's *SQL_CALC_FOUND_ROWS* hint. Note that setting the option
  *     will disable a few LIMIT optimizations and may lead to more documents being processed,
  *     and thus make queries run longer. Note that the *fullCount* attribute may only
  *     be present in the result if the query has a top-level LIMIT clause and the LIMIT 
  *     clause is actually used in the query.
  *     - **intermediateCommitSize**: Maximum total size of operations after which an intermediate commit is performed
  *     automatically. Honored by the RocksDB storage engine only.
  *     - **optimizer.rules** (string): A list of to-be-included or to-be-excluded optimizer rules
  *     can be put into this attribute, telling the optimizer to include or exclude
  *     specific rules. To disable a rule, prefix its name with a `-`, to enable a rule, prefix it
  *     with a `+`. There is also a pseudo-rule `all`, which will match all optimizer rules.
  *     - **maxPlans**: Limits the maximum number of plans that are created by the AQL query optimizer.
  * 
  * 
  * 
  * 
  * The query details include the query string plus optional query options and
  * bind parameters. These values need to be passed in a JSON representation in
  * the body of the POST request.
  * 
  * 
  * **HTTP 201**
  * *A json document with these Properties is returned:*
  * 
  * is returned if the result set can be created by the server.
  * 
  * - **count**: the total number of result documents available (only
  * available if the query was executed with the *count* attribute set)
  * - **code**: the HTTP status code
  * - **extra**: an optional JSON object with extra information about the query result
  * contained in its *stats* sub-attribute. For data-modification queries, the
  * *extra.stats* sub-attribute will contain the number of modified documents and
  * the number of documents that could not be modified
  * due to an error (if *ignoreErrors* query option is specified)
  * - **cached**: a boolean flag indicating whether the query result was served
  * from the query cache or not. If the query result is served from the query
  * cache, the *extra* return attribute will not contain any *stats* sub-attribute
  * and no *profile* sub-attribute.
  * - **hasMore**: A boolean indicator whether there are more results
  * available for the cursor on the server
  * - **result** (anonymous json object): an array of result documents (might be empty if query has no results)
  * - **error**: A flag to indicate that an error occurred (*false* in this case)
  * - **id**: id of temporary cursor created on the server (optional, see above)
  * 
  * 
  * **HTTP 400**
  * *A json document with these Properties is returned:*
  * 
  * is returned if the JSON representation is malformed or the query specification is
  * missing from the request.
  * If the JSON representation is malformed or the query specification is
  * missing from the request, the server will respond with *HTTP 400*.
  * The body of the response will contain a JSON object with additional error
  * details. The object has the following attributes:
  * 
  * - **errorMessage**: a descriptive error message
  * If the query specification is complete, the server will process the query. If an
  * error occurs during query processing, the server will respond with *HTTP 400*.
  * Again, the body of the response will contain details about the error.
  * A [list of query errors can be found here](../../Manual/Appendix/ErrorCodes.html).
  * - **errorNum**: the server error number
  * - **code**: the HTTP status code
  * - **error**: boolean flag to indicate that an error occurred (*true* in this case)
  * 
  * 
  * 
  * 
  * **Example:**
  *  Execute a query and extract the result in a single go
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/cursor</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"query"</span> : <span class="hljs-string">"FOR p IN products LIMIT 2 RETURN p"</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"batchSize"</span> : <span class="hljs-number">2</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103419"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103419"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1M3u--_"</span>, 
  * </code><code>      <span class="hljs-string">"hello1"</span> : <span class="hljs-string">"world1"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103423"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103423"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1M3u--B"</span>, 
  * </code><code>      <span class="hljs-string">"hello2"</span> : <span class="hljs-string">"world1"</span> 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"hasMore"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-number">2</span>, 
  * </code><code>  <span class="hljs-string">"cached"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"extra"</span> : { 
  * </code><code>    <span class="hljs-string">"stats"</span> : { 
  * </code><code>      <span class="hljs-string">"writesExecuted"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"writesIgnored"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"scannedFull"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"scannedIndex"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"filtered"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"httpRequests"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"executionTime"</span> : <span class="hljs-number">0.0001518726348876953</span>, 
  * </code><code>      <span class="hljs-string">"peakMemoryUsage"</span> : <span class="hljs-number">18072</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"warnings"</span> : [ ] 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Execute a query and extract a part of the result
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/cursor</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"query"</span> : <span class="hljs-string">"FOR p IN products LIMIT 5 RETURN p"</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"batchSize"</span> : <span class="hljs-number">2</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103394"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103394"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1M2q--D"</span>, 
  * </code><code>      <span class="hljs-string">"hello2"</span> : <span class="hljs-string">"world1"</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"_key"</span> : <span class="hljs-string">"103403"</span>, 
  * </code><code>      <span class="hljs-string">"_id"</span> : <span class="hljs-string">"products/103403"</span>, 
  * </code><code>      <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1M2u--D"</span>, 
  * </code><code>      <span class="hljs-string">"hello5"</span> : <span class="hljs-string">"world1"</span> 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"hasMore"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"id"</span> : <span class="hljs-string">"103406"</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-number">5</span>, 
  * </code><code>  <span class="hljs-string">"extra"</span> : { 
  * </code><code>    <span class="hljs-string">"stats"</span> : { 
  * </code><code>      <span class="hljs-string">"writesExecuted"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"writesIgnored"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"scannedFull"</span> : <span class="hljs-number">5</span>, 
  * </code><code>      <span class="hljs-string">"scannedIndex"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"filtered"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"httpRequests"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"executionTime"</span> : <span class="hljs-number">0.00016999244689941406</span>, 
  * </code><code>      <span class="hljs-string">"peakMemoryUsage"</span> : <span class="hljs-number">18120</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"warnings"</span> : [ ] 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"cached"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Using the query option "fullCount"
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/cursor</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"query"</span> : <span class="hljs-string">"FOR i IN 1..1000 FILTER i &gt; 500 LIMIT 10 RETURN i"</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"options"</span> : { 
  * </code><code>    <span class="hljs-string">"fullCount"</span> : <span class="hljs-literal">true</span> 
  * </code><code>  } 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : [ 
  * </code><code>    <span class="hljs-number">501</span>, 
  * </code><code>    <span class="hljs-number">502</span>, 
  * </code><code>    <span class="hljs-number">503</span>, 
  * </code><code>    <span class="hljs-number">504</span>, 
  * </code><code>    <span class="hljs-number">505</span>, 
  * </code><code>    <span class="hljs-number">506</span>, 
  * </code><code>    <span class="hljs-number">507</span>, 
  * </code><code>    <span class="hljs-number">508</span>, 
  * </code><code>    <span class="hljs-number">509</span>, 
  * </code><code>    <span class="hljs-number">510</span> 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"hasMore"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-number">10</span>, 
  * </code><code>  <span class="hljs-string">"cached"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"extra"</span> : { 
  * </code><code>    <span class="hljs-string">"stats"</span> : { 
  * </code><code>      <span class="hljs-string">"writesExecuted"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"writesIgnored"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"scannedFull"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"scannedIndex"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"filtered"</span> : <span class="hljs-number">500</span>, 
  * </code><code>      <span class="hljs-string">"httpRequests"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"fullCount"</span> : <span class="hljs-number">500</span>, 
  * </code><code>      <span class="hljs-string">"executionTime"</span> : <span class="hljs-number">0.0007300376892089844</span>, 
  * </code><code>      <span class="hljs-string">"peakMemoryUsage"</span> : <span class="hljs-number">147416</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"warnings"</span> : [ ] 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Enabling and disabling optimizer rules
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/cursor</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"query"</span> : <span class="hljs-string">"FOR i IN 1..10 LET a = 1 LET b = 2 FILTER a + b == 3 RETURN i"</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"options"</span> : { 
  * </code><code>    <span class="hljs-string">"maxPlans"</span> : <span class="hljs-number">1</span>, 
  * </code><code>    <span class="hljs-string">"optimizer"</span> : { 
  * </code><code>      <span class="hljs-string">"rules"</span> : [ 
  * </code><code>        <span class="hljs-string">"-all"</span>, 
  * </code><code>        <span class="hljs-string">"+remove-unnecessary-filters"</span> 
  * </code><code>      ] 
  * </code><code>    } 
  * </code><code>  } 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : [ 
  * </code><code>    <span class="hljs-number">1</span>, 
  * </code><code>    <span class="hljs-number">2</span>, 
  * </code><code>    <span class="hljs-number">3</span>, 
  * </code><code>    <span class="hljs-number">4</span>, 
  * </code><code>    <span class="hljs-number">5</span>, 
  * </code><code>    <span class="hljs-number">6</span>, 
  * </code><code>    <span class="hljs-number">7</span>, 
  * </code><code>    <span class="hljs-number">8</span>, 
  * </code><code>    <span class="hljs-number">9</span>, 
  * </code><code>    <span class="hljs-number">10</span> 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"hasMore"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-number">10</span>, 
  * </code><code>  <span class="hljs-string">"cached"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"extra"</span> : { 
  * </code><code>    <span class="hljs-string">"stats"</span> : { 
  * </code><code>      <span class="hljs-string">"writesExecuted"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"writesIgnored"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"scannedFull"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"scannedIndex"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"filtered"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"httpRequests"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"executionTime"</span> : <span class="hljs-number">0.0001652240753173828</span>, 
  * </code><code>      <span class="hljs-string">"peakMemoryUsage"</span> : <span class="hljs-number">82856</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"warnings"</span> : [ ] 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Execute instrumented query and return result together with
  * execution plan and profiling information
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/cursor</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"query"</span> : <span class="hljs-string">"LET s = SLEEP(0.25) LET t = SLEEP(0.5) RETURN 1"</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"options"</span> : { 
  * </code><code>    <span class="hljs-string">"profile"</span> : <span class="hljs-number">2</span> 
  * </code><code>  } 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : [ 
  * </code><code>    <span class="hljs-number">1</span> 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"hasMore"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-number">1</span>, 
  * </code><code>  <span class="hljs-string">"cached"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"extra"</span> : { 
  * </code><code>    <span class="hljs-string">"plan"</span> : { 
  * </code><code>      <span class="hljs-string">"nodes"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"SingletonNode"</span>, 
  * </code><code>          <span class="hljs-string">"dependencies"</span> : [ ], 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"CalculationNode"</span>, 
  * </code><code>          <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>            <span class="hljs-number">1</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">4</span>, 
  * </code><code>          <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">2</span>, 
  * </code><code>          <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"expression"</span> : { 
  * </code><code>            <span class="hljs-string">"type"</span> : <span class="hljs-string">"value"</span>, 
  * </code><code>            <span class="hljs-string">"typeID"</span> : <span class="hljs-number">40</span>, 
  * </code><code>            <span class="hljs-string">"value"</span> : <span class="hljs-number">1</span>, 
  * </code><code>            <span class="hljs-string">"vType"</span> : <span class="hljs-string">"int"</span>, 
  * </code><code>            <span class="hljs-string">"vTypeID"</span> : <span class="hljs-number">2</span> 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"outVariable"</span> : { 
  * </code><code>            <span class="hljs-string">"id"</span> : <span class="hljs-number">3</span>, 
  * </code><code>            <span class="hljs-string">"name"</span> : <span class="hljs-string">"2"</span> 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"canThrow"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"expressionType"</span> : <span class="hljs-string">"json"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"CalculationNode"</span>, 
  * </code><code>          <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>            <span class="hljs-number">4</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">2</span>, 
  * </code><code>          <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">3</span>, 
  * </code><code>          <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"expression"</span> : { 
  * </code><code>            <span class="hljs-string">"type"</span> : <span class="hljs-string">"function call"</span>, 
  * </code><code>            <span class="hljs-string">"typeID"</span> : <span class="hljs-number">47</span>, 
  * </code><code>            <span class="hljs-string">"name"</span> : <span class="hljs-string">"SLEEP"</span>, 
  * </code><code>            <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>              { 
  * </code><code>                <span class="hljs-string">"type"</span> : <span class="hljs-string">"array"</span>, 
  * </code><code>                <span class="hljs-string">"typeID"</span> : <span class="hljs-number">41</span>, 
  * </code><code>                <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>                  { 
  * </code><code>                    <span class="hljs-string">"type"</span> : <span class="hljs-string">"value"</span>, 
  * </code><code>                    <span class="hljs-string">"typeID"</span> : <span class="hljs-number">40</span>, 
  * </code><code>                    <span class="hljs-string">"value"</span> : <span class="hljs-number">0.25</span>, 
  * </code><code>                    <span class="hljs-string">"vType"</span> : <span class="hljs-string">"double"</span>, 
  * </code><code>                    <span class="hljs-string">"vTypeID"</span> : <span class="hljs-number">3</span> 
  * </code><code>                  } 
  * </code><code>                ] 
  * </code><code>              } 
  * </code><code>            ] 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"outVariable"</span> : { 
  * </code><code>            <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span>, 
  * </code><code>            <span class="hljs-string">"name"</span> : <span class="hljs-string">"s"</span> 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"canThrow"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"expressionType"</span> : <span class="hljs-string">"simple"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"CalculationNode"</span>, 
  * </code><code>          <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>            <span class="hljs-number">2</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">3</span>, 
  * </code><code>          <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">4</span>, 
  * </code><code>          <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"expression"</span> : { 
  * </code><code>            <span class="hljs-string">"type"</span> : <span class="hljs-string">"function call"</span>, 
  * </code><code>            <span class="hljs-string">"typeID"</span> : <span class="hljs-number">47</span>, 
  * </code><code>            <span class="hljs-string">"name"</span> : <span class="hljs-string">"SLEEP"</span>, 
  * </code><code>            <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>              { 
  * </code><code>                <span class="hljs-string">"type"</span> : <span class="hljs-string">"array"</span>, 
  * </code><code>                <span class="hljs-string">"typeID"</span> : <span class="hljs-number">41</span>, 
  * </code><code>                <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>                  { 
  * </code><code>                    <span class="hljs-string">"type"</span> : <span class="hljs-string">"value"</span>, 
  * </code><code>                    <span class="hljs-string">"typeID"</span> : <span class="hljs-number">40</span>, 
  * </code><code>                    <span class="hljs-string">"value"</span> : <span class="hljs-number">0.5</span>, 
  * </code><code>                    <span class="hljs-string">"vType"</span> : <span class="hljs-string">"double"</span>, 
  * </code><code>                    <span class="hljs-string">"vTypeID"</span> : <span class="hljs-number">3</span> 
  * </code><code>                  } 
  * </code><code>                ] 
  * </code><code>              } 
  * </code><code>            ] 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"outVariable"</span> : { 
  * </code><code>            <span class="hljs-string">"id"</span> : <span class="hljs-number">1</span>, 
  * </code><code>            <span class="hljs-string">"name"</span> : <span class="hljs-string">"t"</span> 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"canThrow"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"expressionType"</span> : <span class="hljs-string">"simple"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"ReturnNode"</span>, 
  * </code><code>          <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>            <span class="hljs-number">3</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">5</span>, 
  * </code><code>          <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">5</span>, 
  * </code><code>          <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"inVariable"</span> : { 
  * </code><code>            <span class="hljs-string">"id"</span> : <span class="hljs-number">3</span>, 
  * </code><code>            <span class="hljs-string">"name"</span> : <span class="hljs-string">"2"</span> 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"count"</span> : <span class="hljs-literal">true</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"rules"</span> : [ 
  * </code><code>        <span class="hljs-string">"move-calculations-up"</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"collections"</span> : [ ], 
  * </code><code>      <span class="hljs-string">"variables"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">3</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"2"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"t"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"s"</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">5</span>, 
  * </code><code>      <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>      <span class="hljs-string">"initialize"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"isModificationQuery"</span> : <span class="hljs-literal">false</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"stats"</span> : { 
  * </code><code>      <span class="hljs-string">"writesExecuted"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"writesIgnored"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"scannedFull"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"scannedIndex"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"filtered"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"httpRequests"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"executionTime"</span> : <span class="hljs-number">0.7833671569824219</span>, 
  * </code><code>      <span class="hljs-string">"peakMemoryUsage"</span> : <span class="hljs-number">2312</span>, 
  * </code><code>      <span class="hljs-string">"nodes"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"calls"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"items"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"runtime"</span> : <span class="hljs-number">0.000001430511474609375</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">2</span>, 
  * </code><code>          <span class="hljs-string">"calls"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"items"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"runtime"</span> : <span class="hljs-number">0.2710738182067871</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">3</span>, 
  * </code><code>          <span class="hljs-string">"calls"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"items"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"runtime"</span> : <span class="hljs-number">0.7831518650054932</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">4</span>, 
  * </code><code>          <span class="hljs-string">"calls"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"items"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"runtime"</span> : <span class="hljs-number">0.0000030994415283203125</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">5</span>, 
  * </code><code>          <span class="hljs-string">"calls"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"items"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"runtime"</span> : <span class="hljs-number">0.7831592559814453</span> 
  * </code><code>        } 
  * </code><code>      ] 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"warnings"</span> : [ ], 
  * </code><code>    <span class="hljs-string">"profile"</span> : { 
  * </code><code>      <span class="hljs-string">"initializing"</span> : <span class="hljs-number">7.152557373046875e-7</span>, 
  * </code><code>      <span class="hljs-string">"parsing"</span> : <span class="hljs-number">0.0000171661376953125</span>, 
  * </code><code>      <span class="hljs-string">"optimizing ast"</span> : <span class="hljs-number">0.0000019073486328125</span>, 
  * </code><code>      <span class="hljs-string">"loading collections"</span> : <span class="hljs-number">0.000001430511474609375</span>, 
  * </code><code>      <span class="hljs-string">"instantiating plan"</span> : <span class="hljs-number">0.0000069141387939453125</span>, 
  * </code><code>      <span class="hljs-string">"optimizing plan"</span> : <span class="hljs-number">0.00004315376281738281</span>, 
  * </code><code>      <span class="hljs-string">"executing"</span> : <span class="hljs-number">0.7831752300262451</span>, 
  * </code><code>      <span class="hljs-string">"finalizing"</span> : <span class="hljs-number">0.0001163482666015625</span> 
  * </code><code>    } 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Execute a data-modification query and retrieve the number of
  * modified documents
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/cursor</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"query"</span> : <span class="hljs-string">"FOR p IN products REMOVE p IN products"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : [ ], 
  * </code><code>  <span class="hljs-string">"hasMore"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"cached"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"extra"</span> : { 
  * </code><code>    <span class="hljs-string">"stats"</span> : { 
  * </code><code>      <span class="hljs-string">"writesExecuted"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"writesIgnored"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"scannedFull"</span> : <span class="hljs-number">2</span>, 
  * </code><code>      <span class="hljs-string">"scannedIndex"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"filtered"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"httpRequests"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"executionTime"</span> : <span class="hljs-number">0.0001308917999267578</span>, 
  * </code><code>      <span class="hljs-string">"peakMemoryUsage"</span> : <span class="hljs-number">18040</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"warnings"</span> : [ ] 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Execute a data-modification query with option *ignoreErrors*
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/cursor</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"query"</span> : <span class="hljs-string">"REMOVE 'bar' IN products OPTIONS { ignoreErrors: true }"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : [ ], 
  * </code><code>  <span class="hljs-string">"hasMore"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"cached"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"extra"</span> : { 
  * </code><code>    <span class="hljs-string">"stats"</span> : { 
  * </code><code>      <span class="hljs-string">"writesExecuted"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"writesIgnored"</span> : <span class="hljs-number">1</span>, 
  * </code><code>      <span class="hljs-string">"scannedFull"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"scannedIndex"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"filtered"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"httpRequests"</span> : <span class="hljs-number">0</span>, 
  * </code><code>      <span class="hljs-string">"executionTime"</span> : <span class="hljs-number">0.00013828277587890625</span>, 
  * </code><code>      <span class="hljs-string">"peakMemoryUsage"</span> : <span class="hljs-number">1944</span> 
  * </code><code>    }, 
  * </code><code>    <span class="hljs-string">"warnings"</span> : [ ] 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Bad query - Missing body
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/cursor</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Bad Request
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"query is empty"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">400</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1502</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Bad query - Unknown collection
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/cursor</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"query"</span> : <span class="hljs-string">"FOR u IN unknowncoll LIMIT 2 RETURN u"</span>, 
  * </code><code>  <span class="hljs-string">"count"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"batchSize"</span> : <span class="hljs-number">2</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Not Found
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"AQL: collection or view not found: unknowncoll (while parsing)"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">404</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1203</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Bad query - Execute a data-modification query that attempts to remove a non-existing
  * document
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/cursor</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"query"</span> : <span class="hljs-string">"REMOVE 'foo' IN products"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Not Found
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"AQL: document not found (while executing)"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">404</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1202</span> 
  * </code><code>}
  * </code></pre>
  */
  def post(client: HttpClient, body: PostAPICursor)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/cursor", append = true)
    .restful[PostAPICursor, Json](body)
}