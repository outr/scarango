package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APISimpleAllKeys {

  def put(client: HttpClient, collection: Option[String] = None, body: PutReadAllDocuments)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/simple/all-keys", append = true) 
    .param[Option[String]]("collection", collection, None)
    .restful[PutReadAllDocuments, Json](body)
}