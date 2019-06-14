package com.outr.arango

import scala.concurrent.{ExecutionContext, Future}

case class Pagination[D](builder: QueryBuilder[D],
                         response: QueryResponse[D],
                         offset: Int = 0)
                        (implicit ec: ExecutionContext) {
  lazy val start: Int = offset
  lazy val end: Int = math.max(offset, offset + response.result.size - 1)
  def results: List[D] = response.result
  def total: Int = response.count
  def hasNext: Boolean = response.hasMore
  def next(): Future[Pagination[D]] = if (response.hasMore) {
    builder.get(response.id.get).map(r => copy(response = r, offset = end + 1))
  } else {
    Future.failed(throw new RuntimeException("No more results."))
  }
}
