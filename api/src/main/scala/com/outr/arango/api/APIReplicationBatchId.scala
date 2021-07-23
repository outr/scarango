package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIReplicationBatchId {

  def delete(client: HttpClient, id: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Delete)
    .path(path"/_api/replication/batch/{id}".withArguments(Map("id" -> id)), append = true)
    .call[Value]


  def put(client: HttpClient, body: PutBatchReplication, id: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Put)
    .path(path"/_api/replication/batch/{id}".withArguments(Map("id" -> id)), append = true)
    .restful[PutBatchReplication, Value](body)
}