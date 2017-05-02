package com.outr.arango

import io.circe.{Decoder, Encoder}
import io.youi.http.{HttpResponse, Method}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ArangoDB(val session: ArangoSession, db: String) {
  protected[arango] def restful[Request, Response](name: String,
                                                   request: Request,
                                                   params: Map[String, String] = Map.empty,
                                                   errorHandler: HttpResponse => Response = session.instance.defaultErrorHandler[Response],
                                                   method: Method = Method.Post)
                                                  (implicit encoder: Encoder[Request], decoder: Decoder[Response]): Future[Response] = {
    session.instance.restful[Request, Response](s"/_db/$db/_api/$name", request, Some(session.token), params, errorHandler, method)
  }

  protected[arango] def call[Response](name: String,
                                       method: Method,
                                       params: Map[String, String] = Map.empty,
                                       errorHandler: HttpResponse => Response = session.instance.defaultErrorHandler[Response])
                                      (implicit decoder: Decoder[Response]): Future[Response] = {
    session.instance.call[Response](s"/_db/$db/_api/$name", method, Some(session.token), params, errorHandler)
  }

  def collection(name: String): ArangoCollection = new ArangoCollection(this, name)

  lazy val cursor: ArangoCursor = new ArangoCursor(this)

  def call[T](query: Query)(implicit decoder: Decoder[T]): Future[T] = {
    cursor[T](query, count = true).map { response =>
      assert(response.count.contains(1), s"Response did not include exactly one result: ${response.count}.")
      response.result.head
    }
  }

  lazy val graph: ArangoGraphs = new ArangoGraphs(this)
}