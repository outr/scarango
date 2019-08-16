package com.outr.arango

import com.outr.arango.Value._
import com.outr.arango.api.model.{PostAPICursor, PostAPICursorOpts}
import com.outr.arango.api.{APICursor, APICursorCursorIdentifier}
import io.circe.Decoder.Result
import io.circe.{Decoder, HCursor, Json}
import io.youi.client.HttpClient
import profig.JsonUtil

import scala.concurrent.{ExecutionContext, Future}
import scala.language.experimental.macros

case class QueryBuilder[R](client: HttpClient,
                           query: Query,
                           conversion: Json => R,
                           batchSize: Int = 100,
                           cache: Boolean = true,
                           count: Boolean = false,
                           memoryLimit: Option[Long] = None,
                           options: Option[PostAPICursorOpts] = None,
                           ttl: Option[Long] = None) {
  def as[D](conversion: Json => D): QueryBuilder[D] = copy[D](conversion = conversion)
  def as[D](serialization: Serialization[D]): QueryBuilder[D] = as[D](serialization.fromJson(_))
  def as[D]: QueryBuilder[D] = macro GraphMacros.queryBuilderAs[D]

  def batchSize(batchSize: Int): QueryBuilder[R] = copy(batchSize = batchSize)
  def includeCount: QueryBuilder[R] = copy(count = true)
  def excludeCount: QueryBuilder[R] = copy(count = false)

  private implicit lazy val dDecoder: Decoder[R] = new Decoder[R] {
    override def apply(c: HCursor): Result[R] = Right(conversion(c.value))
  }
  private lazy val qrDecoder: Decoder[QueryResponse[R]] = JsonUtil.decoder[QueryResponse[R]]

  def cursor(implicit ec: ExecutionContext): Future[QueryResponse[R]] = {
    val bindVars = query.bindVars
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
      .map(_.as[QueryResponse[R]](qrDecoder))
      .map {
        case Left(df) => throw df
        case Right(r) => r
      }
  }

  def results(implicit ec: ExecutionContext): Future[List[R]] = cursor(ec).map(_.result)

  def get(id: String)(implicit ec: ExecutionContext): Future[QueryResponse[R]] = APICursorCursorIdentifier
    .put(client, id)
    .map(_.as[QueryResponse[R]](qrDecoder))
    .map {
      case Left(df) => throw df
      case Right(r) => r
    }

  /**
    * Convenience method that calls `cursor` grabbing the first result returning None if there are no results.
    */
  def first(implicit ec: ExecutionContext): Future[Option[R]] = {
    batchSize(1).cursor.map(_.result.headOption)
  }

  def one(implicit ec: ExecutionContext): Future[R] = {
    batchSize(1).includeCount.cursor.map { response =>
      if (response.count != 1) {
        throw new RuntimeException(s"Expected exactly one result for $query, but received ${response.count}")
      } else {
        response.result.head
      }
    }
  }

  def paged(implicit ec: ExecutionContext): Future[Pagination[R]] = {
    includeCount.cursor.map(Pagination(this, _))
  }

  /**
    * Utilizes pagination to process through all pages of data
    *
    * @param f the function to handle processing of each page of data
    * @param ec the ExecutionContext
    * @return List[Return]
    */
  def process[Return](f: QueryResponse[R] => Future[Return])(implicit ec: ExecutionContext): Future[List[Return]] = {
    paged(ec).flatMap(_.process(f))
  }
}