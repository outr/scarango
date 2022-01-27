package com.outr.arango.collection

import cats.effect.IO
import com.outr.arango.Graph
import com.outr.arango.query.Query
import fabric.rw._

case class QueryBuilder[R](graph: Graph, query: Query, rw: ReaderWriter[R]) {
  /**
    * Translates the results to a return type of T
    *
    * @param rw the ReaderWriter for conversion
    * @tparam T return type
    * @return QueryBuilder[T]
    */
  def as[T](implicit rw: ReaderWriter[T]): QueryBuilder[T] = copy[T](rw = rw)

  /**
    * Creates a Stream to get all the results from the query
    *
    * @return fs2.Stream[IO, R]
    */
  def stream: fs2.Stream[IO, R] = graph
    .db
    .query(query)
    .map(_.as[R](rw))

  /**
    * Convenience method to get the results from the stream as a List
    */
  def all: IO[List[R]] = stream.compile.toList

  /**
    * Retrieves exactly one result from the query. If there is zero or more than one an exception will be thrown.
    *
    * @return IO[R]
    */
  def one: IO[R] = all.map {
    case Nil => throw new RuntimeException("No results")
    case d :: Nil => d
    case list => throw new RuntimeException(s"More than one result returned: $list")
  }

  /**
    * The first result from the stream if there are any results.
    */
  def first: IO[Option[R]] = stream.take(1).compile.last

  /**
    * The last result from the stream if there are any results.
    */
  def last: IO[Option[R]] = stream.compile.last
}
