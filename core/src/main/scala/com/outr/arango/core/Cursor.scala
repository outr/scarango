package com.outr.arango.core

import cats.effect.IO
import com.arangodb.ArangoCursor
import fabric.Json

import scala.jdk.CollectionConverters._

case class Cursor[T](id: String,
                     nextBatchId: String,
                     arangoCursor: ArangoCursor[Json],
                     converter: Json => T) {
  def jsonIterator: Iterator[Json] = arangoCursor.asInstanceOf[java.util.Iterator[Json]].asScala
  def jsonStream(chunkSize: Int = 512): fs2.Stream[IO, Json] = fs2.Stream.fromIterator[IO](jsonIterator, chunkSize)

  def iterator: Iterator[T] = jsonIterator.map(converter)
  def stream(chunkSize: Int = 512): fs2.Stream[IO, T] = fs2.Stream.fromIterator[IO](iterator, chunkSize)

  lazy val toList: List[T] = iterator.toList
  def as[R](converter: Json => R): Cursor[R] = copy[R](converter = converter)

  lazy val count: Long = arangoCursor.getCount.toLong
  lazy val fullCount: Long = arangoCursor.getStats.getFullCount
}