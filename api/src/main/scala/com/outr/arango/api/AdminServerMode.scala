package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object AdminServerMode {

  def get(client: HttpClient)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Get)
    .path(path"/_admin/server/mode", append = true) 
    .call[Value]


  def put(client: HttpClient, body: PutAdminServerMode)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Put)
    .path(path"/_admin/server/mode", append = true) 
    .restful[PutAdminServerMode, Value](body)
}