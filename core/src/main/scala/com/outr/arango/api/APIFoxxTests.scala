package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIFoxxTests(client: HttpClient) {
  /**
  * Runs the tests for the service at the given mount path and returns the results.
  * 
  * Supported test reporters are:
  * 
  * - *default*: a simple list of test cases
  * - *suite*: an object of test cases nested in suites
  * - *stream*: a raw stream of test results
  * - *xunit*: an XUnit/JUnit compatible structure
  * - *tap*: a raw TAP compatible stream
  * 
  * The *Accept* request header can be used to further control the response format:
  * 
  * When using the *stream* reporter `application/x-ldjson` will result
  * in the response body being formatted as a newline-delimited JSON stream.
  * 
  * When using the *tap* reporter `text/plain` or `text/{@literal *}` will result
  * in the response body being formatted as a plain text TAP report.
  * 
  * When using the *xunit* reporter `application/xml` or `text/xml` will result
  * in the response body being formatted as XML instead of JSONML.
  * 
  * Otherwise the response body will be formatted as non-prettyprinted JSON.
  */
  def post(mount: String, reporter: Option[String] = None, idiomatic: Option[Boolean] = None, filter: Option[String] = None): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/foxx/tests", append = true) 
    .params("mount" -> mount.toString)
    .param[Option[String]]("reporter", reporter, None)
    .param[Option[Boolean]]("idiomatic", idiomatic, None)
    .param[Option[String]]("filter", filter, None)
    .call[Json]
}