package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class AdminServerRole(client: HttpClient) {
  /**
  * Returns the role of a server in a cluster.
  * The role is returned in the *role* attribute of the result.
  * Possible return values for *role* are:
  * - *SINGLE*: the server is a standalone server without clustering
  * - *COORDINATOR*: the server is a Coordinator in a cluster
  * - *PRIMARY*: the server is a DBServer in a cluster
  * - *SECONDARY*: this role is not used anymore
  * - *AGENT*: the server is an Agency node in a cluster
  * - *UNDEFINED*: in a cluster, *UNDEFINED* is returned if the server role cannot be
  *    determined.
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * Is returned in all cases.
  * 
  * - **errorNum**: the server error number
  * - **code**: the HTTP status code, always 200
  * - **role**: one of [ *SINGLE*, *COORDINATOR*, *PRIMARY*, *SECONDARY*, *AGENT*, *UNDEFINED*]
  * - **error**: always *false*
  */
  def get(): Future[GetAdminServerRoleRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_admin/server/role".withArguments(Map()))
    .call[GetAdminServerRoleRc200]
}