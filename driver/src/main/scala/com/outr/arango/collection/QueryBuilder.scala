package com.outr.arango.collection

import cats.effect.IO
import com.outr.arango.Graph
import com.outr.arango.core.Cursor
import com.outr.arango.query.{Query, QueryOptions, QueryOptionsSupport}
import fabric.Json
import fabric.rw._

case class QueryBuilder[R](graph: Graph,
                           query: Query,
                           converter: Json => R) extends QueryOptionsSupport[QueryBuilder[R]] {
  override def withOptions(f: QueryOptions => QueryOptions): QueryBuilder[R] = copy(query = query.withOptions(f))

  /**
    * Translates the results to a return type of T
    *
    * @param rw the RW for conversion
    * @tparam T return type
    * @return QueryBuilder[T]
    */
  def as[T](implicit rw: RW[T]): QueryBuilder[T] = QueryBuilder[T](graph, query, rw.write)

  /**
    * Retrieves the results as a Cursor.
    */
  def cursor: IO[Cursor[R]] = graph.db.query
    .createCursor(query)
    .map(_.as[R](converter))

  /**
    * Retrieves the next page of a cursor.
    *
    * @param cursorId the cursor id
    */
  def cursorNext(cursorId: String): IO[Cursor[R]] =
    graph.db.query.nextCursor(cursorId).map(_.as[R](converter))

  /**
    * Creates a Stream to get all the results from the query
    *
    * @return fs2.Stream[IO, R]
    */
  def stream(chunkSize: Int = 512): fs2.Stream[IO, R] = fs2.Stream.force(cursor.map(_.stream(chunkSize)))

  def iterator: IO[Iterator[R]] = cursor.map(_.iterator)

  /**
    * Convenience method to get the results from the stream as a List
    */
  def toList: IO[List[R]] = cursor.map(_.toList)

  /**
    * Retrieves exactly one result from the query. If there is zero or more than one an exception will be thrown.
    *
    * @return IO[R]
    */
  def one: IO[R] = toList.map {
    case Nil => throw new RuntimeException("No results")
    case d :: Nil => d
    case list => throw new RuntimeException(s"More than one result returned: $list")
  }

  /**
    * The first result from the stream if there are any results.
    */
  def first: IO[Option[R]] = stream().take(1).compile.last

  /**
    * The last result from the stream if there are any results.
    */
  def last: IO[Option[R]] = stream().compile.last

  /**
    * Streams the result to return a count. A query that generates a count would be more efficient.
    */
  def count: IO[Int] = stream().compile.count.map(_.toInt)

  override def toString: String = query.toString
}