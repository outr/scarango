package com.outr.arango.collection

import cats.effect.IO
import com.outr.arango.Graph
import com.outr.arango.core.{Cursor, QueryOptions}
import com.outr.arango.query.Query
import com.outr.arango.queue.DBQueue
import fabric.Json
import fabric.rw._

import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.duration.{DurationInt, FiniteDuration}

class QueryBuilder[R](graph: Graph, val query: Query, val converter: Json => R) {
  /**
    * Translates the results to a return type of T
    *
    * @param rw the RW for conversion
    * @tparam T return type
    * @return QueryBuilder[T]
    */
  def as[T](implicit rw: RW[T]): QueryBuilder[T] = new QueryBuilder[T](graph, query, rw.write)

  /**
    * Retrieves the results as a Cursor.
    *
    * @param batchSize the page size to use (the number of records per page, defaults to 512)
    * @param ttl the time the cursor will live (defaults to 30 seconds)
    * @param streaming if set to true, one page will be loaded at a time and the full count may not be known but the
    *                  query should executed quite a bit faster
    */
  def cursor(batchSize: Int = 512,
                ttl: FiniteDuration = 30.seconds,
                streaming: Boolean = false): IO[Cursor[R]] = {
    val options = QueryOptions(
      count = Some(true),
      batchSize = Some(batchSize),
      ttl = Some(ttl),
      fullCount = Some(true),
      allowRetry = Some(true),
      stream = Some(streaming)
    )
    graph.db.query.createCursor(query, options).map(_.as[R](converter))
  }

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
  def stream: fs2.Stream[IO, R] = graph
    .db
    .query(query)
    .map(converter)

  def iterator: IO[Iterator[R]] = graph.db.query.iterator(query).map(_.map(converter))

  /**
    * Convenience method to get the results from the stream as a List
    */
  def toList: IO[List[R]] = iterator.map(_.toList)

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
  def first: IO[Option[R]] = stream.take(1).compile.last

  /**
    * The last result from the stream if there are any results.
    */
  def last: IO[Option[R]] = stream.compile.last

  /**
    * Streams the result to return a count. A query that generates a count would be more efficient.
    */
  def count: IO[Int] = stream.compile.count.map(_.toInt)

  /**
    * Process through the stream with the ability to batch queue db inserts, upserts, and deletes.
    *
    * @param processor the function to handle processing the items in the stream
    * @param batchSize the maximum records to hold in memory for a specific collection and operation
    * @return IO[ProcessStats]
    */
  def process(processor: (DBQueue, R) => IO[DBQueue],
              batchSize: Int = 1000): IO[ProcessStats] = {
    val counter = new AtomicInteger(0)
    stream
      .evalScan(DBQueue(batchSize))((queue, value) => {
        counter.incrementAndGet()
        processor(queue, value)
      })
      .compile
      .lastOrError
      .flatMap { queue =>
        queue.finish().map { _ =>
          ProcessStats(counter.get(), queue.inserted, queue.upserted, queue.deleted)
        }
      }
  }

  override def toString: String = query.toString
}