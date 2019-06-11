package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIQueryQueryId {
  /**
  * Kills a running query. The query will be terminated at the next cancelation
  * point.
  */
  def delete(client: HttpClient, queryId: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/query/{query-id}".withArguments(Map("query-id" -> queryId)), append = true)
    .call[Json]
}