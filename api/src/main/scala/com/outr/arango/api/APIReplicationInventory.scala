package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIReplicationInventory {

  def get(client: HttpClient, includeSystem: Option[Boolean] = None, _global: Option[Boolean] = None, batchId: Double)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Get)
    .path(path"/_api/replication/inventory", append = true) 
    .param[Option[Boolean]]("includeSystem", includeSystem, None)
    .param[Option[Boolean]]("global", _global, None)
    .params("batchId" -> batchId.toString)
    .call[Value]
}