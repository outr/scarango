package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIGharialGraph {

  def delete(client: HttpClient, graph: String, dropCollections: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/gharial/{graph}".withArguments(Map("graph" -> graph)), append = true)
    .param[Option[Boolean]]("dropCollections", dropCollections, None)
    .call[Json]


  def get(client: HttpClient, graph: String)(implicit ec: ExecutionContext): Future[GeneralGraphGetHttpExamplesRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_api/gharial/{graph}".withArguments(Map("graph" -> graph)), append = true)
    .call[GeneralGraphGetHttpExamplesRc200]
}