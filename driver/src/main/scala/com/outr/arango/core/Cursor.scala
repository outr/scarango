package com.outr.arango.core

import com.arangodb.ArangoCursor
import fabric.Json

import scala.jdk.CollectionConverters._

case class Cursor[T](id: String,
                     nextBatchId: String,
                     arangoCursor: ArangoCursor[Json],
                     converter: Json => T) {
  def iterator: Iterator[Json] = arangoCursor.asInstanceOf[java.util.Iterator[Json]].asScala
  lazy val toList: List[T] = iterator.map(converter).toList
  def as[R](converter: Json => R): Cursor[R] = copy[R](converter = converter)

  lazy val resultsCount: Long = arangoCursor.getCount.toLong
  lazy val fullCount: Long = arangoCursor.getStats.getFullCount
}