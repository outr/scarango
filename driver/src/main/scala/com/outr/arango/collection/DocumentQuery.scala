package com.outr.arango.collection

import cats.effect.IO
import com.outr.arango.Document
import com.outr.arango.query.Query
import fabric.rw.ReaderWriter

trait DocumentQuery[D <: Document[D]] {
  /**
    * Creates a Stream to get all the results from the query
    * @param query the query to execute
    * @return fs2.Stream[IO, D]
    */
  def apply(query: Query): fs2.Stream[IO, D]

  /**
    * Creates a Stream to get all the results from the query as a specific result type
    * @param query the query to execute
    * @param rw T must have an implicit ReaderWriter
    * @tparam T the type to return
    * @return fs2.Stream[IO, T]
    */
  def as[T](query: Query)(implicit rw: ReaderWriter[T]): fs2.Stream[IO, T]

  /**
    * Convenience method to get the results from the stream as a List
    * @param query the query to execute
    * @return IO[List[D]]
    */
  def all(query: Query): IO[List[D]]

  /**
    * Retrieves exactly one result from the query. If there is zero or more than one an exception will be thrown.
    * @param query the query execute
    * @return IO[D]
    */
  def one(query: Query): IO[D]

  /**
    * The first result from the stream if there are any results.
    * @param query the query to execute
    * @return IO[Option[D]]
    */
  def first(query: Query): IO[Option[D]]

  /**
    * The last result from the stream if there are any results.
    * @param query the query to execute
    * @return IO[Option[D]]
    */
  def last(query: Query): IO[Option[D]]
}