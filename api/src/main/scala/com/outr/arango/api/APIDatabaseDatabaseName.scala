package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIDatabaseDatabaseName {

  def delete(client: HttpClient, databaseName: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/database/{database-name}".withArguments(Map("database-name" -> databaseName)), append = true)
    .call[Json]
}