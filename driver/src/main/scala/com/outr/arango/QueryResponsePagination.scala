package com.outr.arango

import com.outr.arango.rest.QueryResponse
import io.circe.Decoder

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class QueryResponsePagination[T](cursor: ArangoCursor,
                                      response: QueryResponse[T],
                                      offset: Int = 0)(implicit decoder: Decoder[T]) extends Iterable[T] {
  lazy val start: Int = offset
  lazy val end: Int = math.max(offset, offset + response.result.size - 1)
  def results: List[T] = response.result
  def total: Int = response.count.get
  def hasNext: Boolean = response.hasMore
  def next(): Future[QueryResponsePagination[T]] = if (response.hasMore) {
    cursor.get[T](response.id.get).map(r => copy(response = r, offset = end + 1))
  } else {
    Future.failed(throw new RuntimeException("No more results."))
  }

  override def iterator: Iterator[T] = new QueryResponseIterator[T](this)
}
