package com.outr.arango

import com.outr.arango.rest.{QueryRequest, QueryRequestOptions, QueryResponse}
import io.circe.{Decoder, Json}
import io.circe.generic.auto._
import io.youi.http.Method

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ArangoCursor(arangoDB: ArangoDB) {
  def apply[T](query: Query,
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
          case StringValue(s) => Json.fromString(s)
          case IntValue(i) => Json.fromInt(i)
          case DoubleValue(d) => Json.fromDoubleOrNull(d)
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
    arangoDB.restful[QueryRequest, QueryResponse[T]]("cursor", request)
  }

  def paged[T](query: Query,
               batchSize: Int = 100,
               cache: Option[Boolean] = None,
               memoryLimit: Option[Long] = None,
               ttl: Option[Int] = None,
               options: QueryRequestOptions = QueryRequestOptions())
              (implicit decoder: Decoder[T]): Future[QueryResponsePagination[T]] = {
    val count = true
    apply[T](query, count, Some(batchSize), cache, memoryLimit, ttl, options).map(r => QueryResponsePagination[T](this, r))
  }

  def get[T](id: String)
            (implicit decoder: Decoder[T]): Future[QueryResponse[T]] = {
    arangoDB.call[QueryResponse[T]](s"cursor/$id", Method.Put)
  }
}