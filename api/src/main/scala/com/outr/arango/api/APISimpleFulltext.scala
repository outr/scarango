package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APISimpleFulltext {

  def put(client: HttpClient, body: PutAPISimpleFulltext)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Put)
    .path(path"/_api/simple/fulltext", append = true) 
    .restful[PutAPISimpleFulltext, Value](body)
}