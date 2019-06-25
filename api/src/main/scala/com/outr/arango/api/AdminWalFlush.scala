package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object AdminWalFlush {

  def put(client: HttpClient, waitForSync: Option[Boolean] = None, waitForCollector: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_admin/wal/flush", append = true) 
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[Boolean]]("waitForCollector", waitForCollector, None)
    .call[Json]
}