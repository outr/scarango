package com.outr.arango

import java.nio.charset.Charset

import gigahorse.Config
import gigahorse.support.asynchttpclient.Gigahorse
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ArangoHTTP(url: String) {
  private val entryRegex = """"(.+)":(.+)""".r
  private val snakeRegex = """_([a-z])""".r

  def authenticate(username: String, password: String): Future[AuthenticationResponse] = {
    val http = Gigahorse.http(Config())
    val json = s"""{"username":"$username","password":"$password"}"""
    val request = Gigahorse.url(s"$url/_open/auth").post(json, Charset.forName("utf-8"))
    val future = http.run(request).map { response =>
      val body = response.bodyAsString
      val json = entryRegex.replaceAllIn(body, (regexMatch) => {
        val key = snakeRegex.replaceAllIn(regexMatch.group(1), (snakeMatch) => {
          snakeMatch.group(1).toUpperCase
        })
        s""""$key": ${regexMatch.group(2)}"""
      })
      decode[AuthenticationResponse](json) match {
        case Left(error) => throw new RuntimeException(s"JSON decoding error: $json", error)
        case Right(result) => result
      }
    }
    future.onComplete { _ =>
      http.close()
    }
    future
  }

  object query {
    def parse(token: String, db: String, query: String): Future[ParseResult] = {
      val http = Gigahorse.http(Config())
      val json = QueryRequest(query).asJson.spaces2
      val request = Gigahorse
        .url(s"$url/_db/$db/_api/query")
        .post(json, Charset.forName("utf-8"))
        .withHeaders("Authorization" -> s"bearer $token")
      val future = http.processFull(request).map { response =>
        if (response.status != 200) {
          ParseResult(error = true, code = response.status, parsed = false, collections = Nil, bindVars = Nil, ast = Nil)
        } else {
          val body = response.bodyAsString
          decode[ParseResult](body) match {
            case Left(error) => throw new RuntimeException(s"JSON decoding error: $json", error)
            case Right(result) => result
          }
        }
      }
      future.onComplete { _ =>
        http.close()
      }
      future
    }

    case class QueryRequest(query: String)

    def cursor(token: String, db: String, query: String, count: Boolean, batchSize: Int): Future[String] = {
      val http = Gigahorse.http(Config())
      val json = QueryCursorRequest(query, count, batchSize).asJson.spaces2
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

case class ParseResult(error: Boolean,
                       code: Int,
                       parsed: Boolean,
                       collections: List[String],
                       bindVars: List[String],
                       ast: List[ParsedAST])

case class ParsedAST(`type`: String,
                     name: Option[String],
                     id: Option[Int],
                     subNodes: Option[List[ParsedAST]])