package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIFoxxCommit {

  def post(client: HttpClient, replace: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Post)
    .path(path"/_api/foxx/commit", append = true) 
    .param[Option[Boolean]]("replace", replace, None)
    .call[Value]
}