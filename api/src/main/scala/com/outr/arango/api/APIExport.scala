package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIExport {

  def post(client: HttpClient, body: PostAPIExport, collection: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/export", append = true) 
    .params("collection" -> collection.toString)
    .restful[PostAPIExport, Json](body)
}