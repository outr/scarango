package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIImportjson {

  def post(client: HttpClient, body: Json, `type`: String, collection: String, fromPrefix: Option[String] = None, toPrefix: Option[String] = None, overwrite: Option[Boolean] = None, waitForSync: Option[Boolean] = None, onDuplicate: Option[String] = None, complete: Option[Boolean] = None, details: Option[Boolean] = None)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/import#json", append = true) 
    .params("type" -> `type`.toString)
    .params("collection" -> collection.toString)
    .param[Option[String]]("fromPrefix", fromPrefix, None)
    .param[Option[String]]("toPrefix", toPrefix, None)
    .param[Option[Boolean]]("overwrite", overwrite, None)
    .param[Option[Boolean]]("waitForSync", waitForSync, None)
    .param[Option[String]]("onDuplicate", onDuplicate, None)
    .param[Option[Boolean]]("complete", complete, None)
    .param[Option[Boolean]]("details", details, None)
    .restful[Json, Json](body)
}