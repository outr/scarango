package com.outr.arango.pagination

import cats.effect.IO
import com.outr.arango.query.Query
import com.outr.arango.{Graph, Id}
import fabric.rw._

case class Page[R](queryId: Id[Query],
                   resultType: ResultType,
                   page: Int,
                   pageSize: Int,
                   total: Int,
                   results: List[PagedResult],
                   graph: Graph with PaginationSupport)
                  (implicit rw: RW[R]) {
  lazy val offset: Int = page * pageSize
  lazy val pages: Int = math.ceil(total.toDouble / pageSize.toDouble).toInt
  lazy val hasPrevious: Boolean = page > 0
  lazy val hasNext: Boolean = page < pages - 1

  lazy val entries: List[Option[R]] = results.map { result =>
    result.data.map(json => json.as[R])
  }

  def previous: IO[Option[Page[R]]] = if (hasPrevious) {
    graph.pagination.load[R](queryId, page - 1, pageSize)
  } else {
    IO.pure(None)
  }

  def next: IO[Option[Page[R]]] = if (hasNext) {
    graph.pagination.load[R](queryId, page + 1, pageSize)
  } else {
    IO.pure(None)
  }
}
