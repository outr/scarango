package com.outr.arango

import com.outr.arango.Value._
import com.outr.arango.api.model.{PostAPICursor, PostAPICursorOpts, PostApiQueryProperties}
import com.outr.arango.api.{APICursor, APICursorCursorIdentifier, APIQuery}
import io.circe.{Decoder, Json}
import io.circe.generic.auto._
import io.youi.client.HttpClient
import profig.JsonUtil

import scala.concurrent.{ExecutionContext, Future}

class ArangoQuery(client: HttpClient) {
  def validate(query: String)(implicit ec: ExecutionContext): Future[ValidationResult] = APIQuery
    .post(client, PostApiQueryProperties(query))
    .map(JsonUtil.fromJson[ValidationResult](_))
    .recover {
      case exc: ArangoException => JsonUtil.fromJsonString[ValidationResult](exc.response.content.get.asString)
    }

  def cursor[D](query: Query,
               batchSize: Int = 100,
               cache: Boolean = true,
               count: Boolean = false,
               memoryLimit: Option[Long] = None,
               options: Option[PostAPICursorOpts] = None,
               ttl: Option[Long] = None)
              (implicit ec: ExecutionContext, serialization: Serialization[D]): Future[QueryResponse[D]] = {
    val bindVars = Json.obj(query.args.map {
      case (key, value) => {
        val argValue: Json = value match {
          case Value.Null => Json.Null
          case StringValue(s) => Json.fromString(s)
          case BooleanValue(b) => Json.fromBoolean(b)
          case IntValue(i) => Json.fromInt(i)
          case LongValue(l) => Json.fromLong(l)
          case DoubleValue(d) => Json.fromDoubleOrNull(d)
          case BigDecimalValue(d) => Json.fromBigDecimal(d)
          case SeqStringValue(l) => Json.fromValues(l.map(Json.fromString))
          case SeqBooleanValue(l) => Json.fromValues(l.map(Json.fromBoolean))
          case SeqIntValue(l) => Json.fromValues(l.map(Json.fromInt))
          case SeqLongValue(l) => Json.fromValues(l.map(Json.fromLong))
          case SeqDoubleValue(l) => Json.fromValues(l.map(Json.fromDoubleOrNull))
          case SeqBigDecimalValue(l) => Json.fromValues(l.map(Json.fromBigDecimal))
        }
        key -> argValue
      }
    }.toSeq: _*)
    implicit val dDecoder: Decoder[D] = serialization.decoder
    APICursor
      .post(
        client = client,
        body = PostAPICursor(
          query = query.value,
          bindVars = bindVars,
          batchSize = Some(batchSize.toLong),
          cache = Some(cache),
          count = Some(count),
          memoryLimit = memoryLimit,
          options = options,
          ttl = ttl
        )
      )
      .map(_.as[QueryResponse[D]])
      .map {
        case Left(df) => throw df
        case Right(r) => r
      }
  }

  def get[D](id: String)(implicit ec: ExecutionContext, serialization: Serialization[D]): Future[QueryResponse[D]] = {
    implicit val dDecoder: Decoder[D] = serialization.decoder
    APICursorCursorIdentifier
      .put(client, id)
      .map(_.as[QueryResponse[D]])
      .map {
        case Left(df) => throw df
        case Right(r) => r
      }
  }

  /**
    * Convenience method that calls `cursor` grabbing the first result returning None if there are no results.
    */
  def first[D](query: Query)
              (implicit ec: ExecutionContext, serialization: Serialization[D]): Future[Option[D]] = {
    cursor[D](query, batchSize = 1).map(_.result.headOption)
  }

  def paged[D](query: Query,
               batchSize: Int = 100,
               cache: Boolean = true,
               count: Boolean = false,
               memoryLimit: Option[Long] = None,
               options: Option[PostAPICursorOpts] = None,
               ttl: Option[Long] = None)
              (implicit ec: ExecutionContext, serialization: Serialization[D]): Future[QueryResponsePagination[D]] = {
    val count = true
    cursor[D](query, batchSize, cache, count, memoryLimit, options, ttl)
      .map(r => QueryResponsePagination[D](this, r))
  }
}

case class QueryResponse[T](id: Option[String],
                            result: List[T],
                            hasMore: Boolean,
                            count: Option[Int],
                            cached: Boolean,
                            extra: QueryResponseExtras,
                            error: Boolean,
                            code: Int)

case class QueryResponseExtras(stats: QueryResponseStats, warnings: List[String])

case class QueryResponseStats(writesExecuted: Int,
                              writesIgnored: Int,
                              scannedFull: Int,
                              scannedIndex: Int,
                              filtered: Int,
                              executionTime: Double)

case class QueryResponsePagination[D](cursor: ArangoQuery,
                                      response: QueryResponse[D],
                                      offset: Int = 0)
                                     (implicit ec: ExecutionContext, serialization: Serialization[D]) {
  lazy val start: Int = offset
  lazy val end: Int = math.max(offset, offset + response.result.size - 1)
  def results: List[D] = response.result
  def total: Int = response.count.get
  def hasNext: Boolean = response.hasMore
  def next(): Future[QueryResponsePagination[D]] = if (response.hasMore) {
    cursor.get[D](response.id.get).map(r => copy(response = r, offset = end + 1))
  } else {
    Future.failed(throw new RuntimeException("No more results."))
  }
}