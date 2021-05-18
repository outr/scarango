package com.outr.arango.api

import com.outr.arango.api.model._
import fabric.Value
import fabric.parse.Json
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._

import scala.concurrent.{ExecutionContext, Future}
      
object APIAnalyzer {
  def get(client: HttpClient)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Get)
    .path(path"/_api/analyzer", append = true) 
    .send()
    .map { response =>
      val json = response.content.map(_.asString).getOrElse("{}")
      Json.parse(json)
    }


  def post(client: HttpClient, body: PostAPIAnalyzer)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Post)
    .path(path"/_api/analyzer", append = true) 
    .restful[PostAPIAnalyzer, Value](body)
}