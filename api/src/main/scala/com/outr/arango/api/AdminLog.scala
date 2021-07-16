package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object AdminLog {

  def get(client: HttpClient, upto: Option[String] = None, level: Option[String] = None, start: Option[Double] = None, size: Option[Double] = None, offset: Option[Double] = None, search: Option[String] = None, sort: Option[String] = None)(implicit ec: ExecutionContext): Future[GetAdminLogRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_admin/log", append = true) 
    .param[Option[String]]("upto", upto, None)
    .param[Option[String]]("level", level, None)
    .param[Option[Double]]("start", start, None)
    .param[Option[Double]]("size", size, None)
    .param[Option[Double]]("offset", offset, None)
    .param[Option[String]]("search", search, None)
    .param[Option[String]]("sort", sort, None)
    .call[GetAdminLogRc200]
}