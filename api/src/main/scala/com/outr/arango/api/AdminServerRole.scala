package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object AdminServerRole {

  def get(client: HttpClient)(implicit ec: ExecutionContext): Future[GetAdminServerRoleRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_admin/server/role", append = true) 
    .call[GetAdminServerRoleRc200]
}