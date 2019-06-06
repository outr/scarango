package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
object AdminLog {
  /**
  * Returns fatal, error, warning or info log messages from the server's global log.
  * The result is a JSON object with the following attributes:
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * - **lid** (string): a list of log entry identifiers. Each log message is uniquely
  * identified by its @LIT{lid} and the identifiers are in ascending
  * order.
  * - **level**: A list of the log levels for all log entries.
  * - **timestamp** (string): a list of the timestamps as seconds since 1970-01-01 for all log
  * entries.
  * - **topic**: a list of the topics of all log entries
  * - **text**: a list of the texts of all log entries
  * - **totalAmount**: the total amount of log entries before pagination.
  */
  def get(client: HttpClient, upto: Option[String] = None, level: Option[String] = None, start: Option[Double] = None, size: Option[Double] = None, offset: Option[Double] = None, search: Option[String] = None, sort: Option[String] = None): Future[GetAdminLogRc200] = client
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