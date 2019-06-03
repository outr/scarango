package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class AdminWalTransactionsGet(client: HttpClient) {
  /**
  * Returns information about the currently running transactions. The result
  * is a JSON object with the following attributes:
  * - *runningTransactions*: number of currently running transactions
  * - *minLastCollected*: minimum id of the last collected logfile (at the
  *   start of each running transaction). This is *null* if no transaction is
  *   running.
  * - *minLastSealed*: minimum id of the last sealed logfile (at the
  *   start of each running transaction). This is *null* if no transaction is
  *   running.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_admin/wal/transactions</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"runningTransactions"</span> : <span class="hljs-number">3</span>, 
  * </code><code>  <span class="hljs-string">"minLastCollected"</span> : <span class="hljs-number">85</span>, 
  * </code><code>  <span class="hljs-string">"minLastSealed"</span> : <span class="hljs-literal">null</span> 
  * </code><code>}
  * </code></pre>
  */
  def get(): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .call[ArangoResponse]
}