package com.outr.arango

import com.outr.arango.rest._
import io.circe.generic.auto._
import io.circe.{Decoder, Encoder, Json}
import io.youi.http.{HttpResponse, Method}

import scala.concurrent.Future

class ArangoDB(val session: ArangoSession, db: String) {
  protected[arango] def restful[Request, Response](name: String,
                                           request: Request,
                                           params: Map[String, String] = Map.empty,
                                           errorHandler: HttpResponse => Response = session.instance.defaultErrorHandler[Response])
                                          (implicit encoder: Encoder[Request], decoder: Decoder[Response]): Future[Response] = {
    session.instance.restful[Request, Response](s"/_db/$db/_api/$name", request, Some(session.token), params, errorHandler)
  }

  protected[arango] def call[Response](name: String,
                               method: Method,
                               params: Map[String, String] = Map.empty,
                               errorHandler: HttpResponse => Response = session.instance.defaultErrorHandler[Response])
                              (implicit decoder: Decoder[Response]): Future[Response] = {
    session.instance.call[Response](s"/_db/$db/_api/$name", method, Some(session.token), params, errorHandler)
  }

  def collection(name: String): ArangoCollection = new ArangoCollection(this, name)

  def cursor(query: String, count: Boolean, batchSize: Int): Future[QueryResponse] = {
    restful[QueryRequest, QueryResponse]("cursor", QueryRequest(query, count, batchSize))
  }
}