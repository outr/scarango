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

  def cursor[T](query: Query,
                count: Boolean = false,
                batchSize: Option[Int] = None,
                cache: Option[Boolean] = None,
                memoryLimit: Option[Long] = None,
                ttl: Option[Int] = None,
                options: QueryRequestOptions = QueryRequestOptions())
               (implicit decoder: Decoder[T]): Future[QueryResponse[T]] = {
    val bindVars = Json.obj(query.args.map {
      case (key, value) => {
        val argValue: Json = value match {
          case QueryArg.string(s) => Json.fromString(s)
          case QueryArg.double(d) => Json.fromDoubleOrNull(d)
          case QueryArg.int(i) => Json.fromInt(i)
        }
        key -> argValue
      }
    }.toSeq: _*)
    val request = QueryRequest(
      query = query.value,
      bindVars = bindVars,
      count = count,
      batchSize = batchSize,
      cache = cache,
      memoryLimit = memoryLimit,
      ttl = ttl,
      options = options
    )
    restful[QueryRequest, QueryResponse[T]]("cursor", request)
  }
}