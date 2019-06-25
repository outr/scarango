package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIAqlfunctionName {

  def delete(client: HttpClient, name: String, group: Option[String] = None)(implicit ec: ExecutionContext): Future[DeleteAPIAqlfunctionRc200] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/aqlfunction/{name}".withArguments(Map("name" -> name)), append = true)
    .param[Option[String]]("group", group, None)
    .call[DeleteAPIAqlfunctionRc200]
}