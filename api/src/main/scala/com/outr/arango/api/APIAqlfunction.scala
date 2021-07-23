package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.{ExecutionContext, Future}
      
object APIAqlfunction {

  def get(client: HttpClient, namespace: Option[String] = None)(implicit ec: ExecutionContext): Future[GetAPIAqlfunctionRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_api/aqlfunction", append = true) 
    .param[Option[String]]("namespace", namespace, None)
    .call[GetAPIAqlfunctionRc200]


  def post(client: HttpClient, body: PostAPIAqlfunction)(implicit ec: ExecutionContext): Future[PostAPIAqlfunctionRc200] = client
    .method(HttpMethod.Post)
    .path(path"/_api/aqlfunction", append = true) 
    .restful[PostAPIAqlfunction, PostAPIAqlfunctionRc200](body)
}