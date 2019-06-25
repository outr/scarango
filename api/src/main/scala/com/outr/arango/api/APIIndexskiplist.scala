package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIIndexskiplist {

  def post(client: HttpClient, collectionName: String, body: PostAPIIndexSkiplist)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/index", append = true)
    .params("collection" -> collectionName.toString)
    .restful[PostAPIIndexSkiplist, Json](body)
}