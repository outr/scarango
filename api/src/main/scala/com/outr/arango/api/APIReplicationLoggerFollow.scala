package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIReplicationLoggerFollow {

  def get(client: HttpClient, from: Option[Double] = None, to: Option[Double] = None, chunkSize: Option[Double] = None, includeSystem: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/replication/logger-follow", append = true) 
    .param[Option[Double]]("from", from, None)
    .param[Option[Double]]("to", to, None)
    .param[Option[Double]]("chunkSize", chunkSize, None)
    .param[Option[Boolean]]("includeSystem", includeSystem, None)
    .call[Json]
}