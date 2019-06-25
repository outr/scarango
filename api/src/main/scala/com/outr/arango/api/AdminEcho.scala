package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object AdminEcho {

  def post(client: HttpClient, body: Json)(implicit ec: ExecutionContext): Future[PostAdminEchoRc200] = client
    .method(HttpMethod.Post)
    .path(path"/_admin/echo", append = true) 
    .restful[Json, PostAdminEchoRc200](body)
}