package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIReplicationBatchId {

  def delete(client: HttpClient, id: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/replication/batch/{id}".withArguments(Map("id" -> id)), append = true)
    .call[Json]


  def put(client: HttpClient, body: PutBatchReplication, id: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/replication/batch/{id}".withArguments(Map("id" -> id)), append = true)
    .restful[PutBatchReplication, Json](body)
}