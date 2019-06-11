package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object AdminExecute {
  /**
  * Executes the javascript code in the body on the server as the body
  * of a function with no arguments. If you have a *return* statement
  * then the return value you produce will be returned as content type
  * *application/json*. If the parameter *returnAsJSON* is set to
  * *true*, the result will be a JSON object describing the return value
  * directly, otherwise a string produced by JSON.stringify will be
  * returned.
  * 
  * Note that this API endpoint will only be present if the server was
  * started with the option `--javascript.allow-admin-execute true`.
  * 
  * The default value of this option is `false`, which disables the execution of 
  * user-defined code and disables this API endpoint entirely. 
  * This is also the recommended setting for production.
  */
  def post(client: HttpClient, body: Json)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_admin/execute", append = true) 
    .restful[Json, Json](body)
}