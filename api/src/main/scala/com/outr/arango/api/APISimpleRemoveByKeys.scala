package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APISimpleRemoveByKeys {

  def put(client: HttpClient, body: RestRemoveByKeys)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/simple/remove-by-keys", append = true) 
    .restful[RestRemoveByKeys, Json](body)
}