package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object AdminEcho {

  def post(client: HttpClient, body: Value)(implicit ec: ExecutionContext): Future[PostAdminEchoRc200] = client
    .method(HttpMethod.Post)
    .path(path"/_admin/echo", append = true) 
    .restful[Value, PostAdminEchoRc200](body)
}