package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIJobJobId {

  def get(client: HttpClient, jobId: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/job/{job-id}".withArguments(Map("job-id" -> jobId)), append = true)
    .call[Json]


  def put(client: HttpClient, jobId: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/job/{job-id}".withArguments(Map("job-id" -> jobId)), append = true)
    .call[Json]
}