package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APIDatabase(client: HttpClient) {
  /**
  * Retrieves the list of all existing databases
  * 
  * **Note**: retrieving the list of databases is only possible from within the *_system* database.
  * 
  * **Note**: You should use the [*GET user API*](../UserManagement/README.md#list-the-accessible-databases-for-a-user) to fetch the list of the available databases now.
  * 
  * 
  * 
  * 
  * **Example:**
  *  
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/database</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span>, 
  * </code><code>  <span class="hljs-string">"result"</span> : [ 
  * </code><code>    <span class="hljs-string">"_system"</span> 
  * </code><code>  ] 
  * </code><code>}
  * </code></pre>
  */
  def get(): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_api/database".withArguments(Map()))
    .call[ArangoResponse]

  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **name**: Has to contain a valid database name.
  *   - **users**: Has to be an array of user objects to initially create for the new database.
  *    User information will not be changed for users that already exist.
  *    If *users* is not specified or does not contain any users, a default user
  *    *root* will be created with an empty string password. This ensures that the
  *    new database will be accessible after it is created.
  *    Each user object can contain the following attributes:
  *     - **username**: Login name of the user to be created
  *     - **passwd**: The user password as a string. If not specified, it will default to an empty string.
  *     - **active**: A flag indicating whether the user account should be activated or not.
  *     The default value is *true*. If set to *false*, the user won't be able to
  *     log into the database.
  *     - **extra**: A JSON object with extra user information. The data contained in *extra*
  *     will be stored for the user but not be interpreted further by ArangoDB.
  * 
  * 
  * 
  * 
  * Creates a new database
  * 
  * The response is a JSON object with the attribute *result* set to *true*.
  * 
  * **Note**: creating a new database is only possible from within the *_system* database.
  * 
  * 
  * 
  * 
  * **Example:**
  *  Creating a database named *example*.
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/database</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"example"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span>, 
  * </code><code>  <span class="hljs-string">"result"</span> : <span class="hljs-literal">true</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Creating a database named *mydb* with two users.
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/database</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"name"</span> : <span class="hljs-string">"mydb"</span>, 
  * </code><code>  <span class="hljs-string">"users"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"username"</span> : <span class="hljs-string">"admin"</span>, 
  * </code><code>      <span class="hljs-string">"passwd"</span> : <span class="hljs-string">"secret"</span>, 
  * </code><code>      <span class="hljs-string">"active"</span> : <span class="hljs-literal">true</span> 
  * </code><code>    }, 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"username"</span> : <span class="hljs-string">"tester"</span>, 
  * </code><code>      <span class="hljs-string">"passwd"</span> : <span class="hljs-string">"test001"</span>, 
  * </code><code>      <span class="hljs-string">"active"</span> : <span class="hljs-literal">false</span> 
  * </code><code>    } 
  * </code><code>  ] 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Created
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">201</span>, 
  * </code><code>  <span class="hljs-string">"result"</span> : <span class="hljs-literal">true</span> 
  * </code><code>}
  * </code></pre>
  */
  def post(body: GetAPIDatabaseNew): Future[ArangoResponse] = client
    .method(HttpMethod.Post)
    .path(path"/_db/_system/_api/database".withArguments(Map()))
    .restful[GetAPIDatabaseNew, ArangoResponse](body)
}