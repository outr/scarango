package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APITraversal {

  def post(client: HttpClient, body: HTTPAPITRAVERSAL)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/traversal", append = true) 
    .restful[HTTPAPITRAVERSAL, Json](body)
}