package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIIndexgeo {

  def post(client: HttpClient, collection: String, body: PostAPIIndexGeo)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Post)
    .path(path"/_api/index", append = true)
    .params("collection" -> collection.toString)
    .restful[PostAPIIndexGeo, Value](body)
}