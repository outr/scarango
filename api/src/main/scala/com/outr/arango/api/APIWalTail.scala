package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIWalTail {

  def get(client: HttpClient, from: Option[Double] = None, to: Option[Double] = None, lastScanned: Option[Double] = None, _global: Option[Boolean] = None, chunkSize: Option[Double] = None, serverId: Option[Double] = None, barrierId: Option[Double] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/wal/tail", append = true) 
    .param[Option[Double]]("from", from, None)
    .param[Option[Double]]("to", to, None)
    .param[Option[Double]]("lastScanned", lastScanned, None)
    .param[Option[Boolean]]("global", _global, None)
    .param[Option[Double]]("chunkSize", chunkSize, None)
    .param[Option[Double]]("serverId", serverId, None)
    .param[Option[Double]]("barrierId", barrierId, None)
    .call[Json]
}