package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APISimpleRemoveByExample {

  def put(client: HttpClient, body: PutAPISimpleRemoveByExample)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Put)
    .path(path"/_api/simple/remove-by-example", append = true) 
    .restful[PutAPISimpleRemoveByExample, Value](body)
}