package com.outr.arango

import java.nio.charset.Charset

import gigahorse.Config
import gigahorse.support.asynchttpclient.Gigahorse
import upickle.Js

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ArangoHTTP(url: String) {
  def authenticate(username: String, password: String): Future[AuthenticationResponse] = {
    val http = Gigahorse.http(Config())
    val json = s"""{"username":"$username","password":"$password"}"""
    val request = Gigahorse.url(s"$url/_open/auth").post(json, Charset.forName("utf-8"))
    val future = http.run(request).map { response =>
      val body = response.bodyAsString
      SnakePickle.read[AuthenticationResponse](body)
    }
    future.onComplete { _ =>
      http.close()
    }
    future
  }

  object query {
    // /_db/mydb/
    def apply(token: String, db: String, query: String): Future[String] = {
      val http = Gigahorse.http(Config())
      val json = upickle.default.write(QueryRequest(query))
      val request = Gigahorse
        .url(s"$url/_db/$db/_api/cursor")
        .post(json, Charset.forName("utf-8"))
        .withHeaders("Authorization" -> s"bearer $token")
      val future = http.run(request).map { response =>
        val body = response.bodyAsString
        body
      }
      future.onComplete { _ =>
        http.close()
      }
      future
    }

    case class QueryRequest(query: String)

    def cursor(token: String, db: String, query: String, count: Boolean, batchSize: Int): Future[String] = {
      val http = Gigahorse.http(Config())
      val json = upickle.default.write(QueryCursorRequest(query, count, batchSize))
      val request = Gigahorse
        .url(s"$url/_db/$db/_api/cursor")
        .post(json, Charset.forName("utf-8"))
        .withHeaders("Authorization" -> s"bearer $token")
      val future = http.run(request).map { response =>
        val body = response.bodyAsString
        body
      }
      future.onComplete { _ =>
        http.close()
      }
      future
    }
  }

  case class QueryCursorRequest(query: String, count: Boolean, batchSize: Int)

  case class AuthenticationResponse(jwt: String, mustChangePassword: Boolean)
}

object SnakePickle extends upickle.AttributeTagged {
  def camelToSnake(s: String): String = {
    val res = s.split("(?=[A-Z])", -1).map(_.toLowerCase).mkString("_")
    res
  }

  override def CaseR[T: Reader, V](f: T => V,
                                        names: Array[String],
                                        defaults: Array[Js.Value]): Reader[V] = {
    super.CaseR[T, V](f, names.map(camelToSnake), defaults)
  }

  override def CaseW[T: Writer, V](f: V => Option[T],
                                        names: Array[String],
                                        defaults: Array[Js.Value]): Writer[V] = {
    super.CaseW[T, V](f, names.map(camelToSnake), defaults)
  }
}