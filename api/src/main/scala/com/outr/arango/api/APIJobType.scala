package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIJobType {

  def delete(client: HttpClient, `type`: String, stamp: Option[Double] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/job/{type}".withArguments(Map("type" -> `type`)), append = true)
    .param[Option[Double]]("stamp", stamp, None)
    .call[Json]


  def get(client: HttpClient, `type`: String, count: Option[Double] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/job/{type}".withArguments(Map("type" -> `type`)), append = true)
    .param[Option[Double]]("count", count, None)
    .call[Json]
}