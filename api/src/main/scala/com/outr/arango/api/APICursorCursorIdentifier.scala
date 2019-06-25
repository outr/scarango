package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APICursorCursorIdentifier {

  def delete(client: HttpClient, cursorIdentifier: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/cursor/{cursor-identifier}".withArguments(Map("cursor-identifier" -> cursorIdentifier)), append = true)
    .call[Json]


  def put(client: HttpClient, cursorIdentifier: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/cursor/{cursor-identifier}".withArguments(Map("cursor-identifier" -> cursorIdentifier)), append = true)
    .call[Json]
}