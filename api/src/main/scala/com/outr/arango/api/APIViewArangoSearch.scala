package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIViewArangoSearch {

  def post(client: HttpClient, body: PostAPIViewIresearch)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/view", append = true)
    .restful[PostAPIViewIresearch, Json](body)
}