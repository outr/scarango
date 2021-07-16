package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APISimpleLookupByKeys {

  def put(client: HttpClient, body: RestLookupByKeys)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Put)
    .path(path"/_api/simple/lookup-by-keys", append = true) 
    .restful[RestLookupByKeys, Value](body)
}