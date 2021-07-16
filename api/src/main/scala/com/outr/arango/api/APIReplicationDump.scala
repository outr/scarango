package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIReplicationDump {

  def get(client: HttpClient, collection: String, chunkSize: Option[Double] = None, batchId: Double, from: Option[Double] = None, to: Option[Double] = None, includeSystem: Option[Boolean] = None, ticks: Option[Boolean] = None, flush: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Get)
    .path(path"/_api/replication/dump", append = true) 
    .params("collection" -> collection.toString)
    .param[Option[Double]]("chunkSize", chunkSize, None)
    .params("batchId" -> batchId.toString)
    .param[Option[Double]]("from", from, None)
    .param[Option[Double]]("to", to, None)
    .param[Option[Boolean]]("includeSystem", includeSystem, None)
    .param[Option[Boolean]]("ticks", ticks, None)
    .param[Option[Boolean]]("flush", flush, None)
    .call[Value]
}