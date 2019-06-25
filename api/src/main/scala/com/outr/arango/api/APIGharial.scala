package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIGharial {

  def get(client: HttpClient)(implicit ec: ExecutionContext): Future[GeneralGraphListHttpExamplesRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_api/gharial", append = true) 
    .call[GeneralGraphListHttpExamplesRc200]


  def post(client: HttpClient, waitForSync: Option[Boolean] = None, body: GeneralGraphCreateHttpExamples)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/gharial", append = true) 
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .restful[GeneralGraphCreateHttpExamples, Json](body)
}