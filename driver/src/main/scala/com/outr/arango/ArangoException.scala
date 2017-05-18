package com.outr.arango

import io.youi.http.{HttpRequest, HttpResponse, RequestContent, StringContent}

class ArangoException(val error: ArangoError,
                      val request: HttpRequest,
                      val response: HttpResponse,
                      val cause: Option[Throwable]) extends RuntimeException(cause.orNull) {
  override def getMessage: String = {
    s"""Error: $error,
       |URL: ${request.url} (${request.method}),
       |Request: ${request.content.map(ArangoException.contentToString).getOrElse("No Content")},
       |Response: ${response.content.map(ArangoException.contentToString).getOrElse("No Content")}
     """.stripMargin.trim
  }
}

object ArangoException {
  def contentToString(content: RequestContent): String = content match {
    case StringContent(value, _, _) => value
    case _ => throw new UnsupportedOperationException(s"Unsupported content conversion: $content.")
  }
}