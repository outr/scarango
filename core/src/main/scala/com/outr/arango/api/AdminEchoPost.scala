package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class AdminEchoPost(client: HttpClient) {
  /**
  * The call returns an object with the servers request information
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * Echo was returned successfully.
  * 
  * - **cookies**: list of the cookies you sent
  * - **protocol**: the transport, one of ['http', 'https', 'velocystream']
  * - **suffix** (anonymous json object): 
  * - **parameters**: Object containing the query parameters
  * - **database**: the database this request was executed on
  * - **url**: the raw request URL
  * - **internals**: contents of the server internals struct
  * - **server**:
  *   - **id**: a server generated id
  *   - **port**: port of the client side of the tcp connection
  *   - **address**: the ip address of the client
  * - **requestBody**: stringified version of the POST body we sent
  * - **headers**: the list of the HTTP headers you sent
  * - **prefix**: prefix of the database
  * - **client**:
  * - **authorized**: whether the session is authorized
  * - **requestType**: In this case *POST*, if you use another HTTP-Verb, you will se that (GET/DELETE, ...)
  * - **rawSuffix** (anonymous json object): 
  * - **path**: relative path of this request
  * - **rawRequestBody** (anonymous json object): List of digits of the sent characters
  * - **user**: the currently user that sent this request
  */
  def post(body: IoCirceJson): Future[PostAdminEchoRc200] = client
    .method(HttpMethod.Post)
    .restful[IoCirceJson, PostAdminEchoRc200](body)
}