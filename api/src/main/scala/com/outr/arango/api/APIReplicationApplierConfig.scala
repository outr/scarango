package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIReplicationApplierConfig {

  def get(client: HttpClient)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Get)
    .path(path"/_api/replication/applier-config", append = true) 
    .call[Value]


  def put(client: HttpClient, body: PutAPIReplicationApplierAdjust)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Put)
    .path(path"/_api/replication/applier-config", append = true) 
    .restful[PutAPIReplicationApplierAdjust, Value](body)
}