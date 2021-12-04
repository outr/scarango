package com.outr.arango

import io.youi.http.{HttpRequest, HttpResponse}

class ArangoException(val error: ArangoError,
                      val request: HttpRequest,
                      val response: HttpResponse,
                      val cause: Option[Throwable]) extends RuntimeException(cause.orNull) {
  override def getMessage: String = {
    s"""URL: ${request.url} (Method: ${request.method}, Status: ${response.status}),
       |Request: ${request.content.map(_.asString).getOrElse("No Content")},
       |Response: ${response.content.map(_.asString).getOrElse("No Content")}
     """.stripMargin.trim
  }
}