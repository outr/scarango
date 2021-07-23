package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import fabric.Value
import scala.concurrent.{ExecutionContext, Future}
      
object APIJobJobIdCancel {

  def put(client: HttpClient, jobId: String)(implicit ec: ExecutionContext): Future[Value] = client
    .method(HttpMethod.Put)
    .path(path"/_api/job/{job-id}/cancel".withArguments(Map("job-id" -> jobId)), append = true)
    .call[Value]
}