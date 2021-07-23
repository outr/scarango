package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APICursorCursorIdentifier {

  def delete(client: HttpClient, cursorIdentifier: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/cursor/{cursor-identifier}".withArguments(Map("cursor-identifier" -> cursorIdentifier)), append = true)
    .call[Value]


  def put(client: HttpClient, cursorIdentifier: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Put)
    .path(path"/_api/cursor/{cursor-identifier}".withArguments(Map("cursor-identifier" -> cursorIdentifier)), append = true)
    .call[Value]
}