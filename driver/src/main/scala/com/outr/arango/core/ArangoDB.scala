package com.outr.arango.core

import cats.effect.IO
import com.arangodb.async.ArangoDatabaseAsync
import com.outr.arango._
import com.outr.arango.util.Helpers._
import fabric.Value

import scala.jdk.CollectionConverters._

class ArangoDB(db: ArangoDatabaseAsync) {
  def name: String = db.name()

  def create(): IO[Boolean] = db.create().toIO.map(_.booleanValue())

  def exists(): IO[Boolean] = db.exists().toIO.map(_.booleanValue())

  def drop(): IO[Boolean] = db.drop().toIO.map(_.booleanValue())

  object query {
    def parse(query: Query): IO[AQLParseResult] = {
      db.parseQuery(query.string).toIO.map(aqlParseEntityConversion)
    }

    def apply(query: Query): fs2.Stream[IO, Value] = {
      val bindVars: java.util.Map[String, AnyRef] = query.variables.map {
        case (key, value) => key -> value2AnyRef(value)
      }.asJava

      fs2.Stream.force(db.query(query.string, bindVars, classOf[String]).toIO.map { c =>
        // TODO: Consider c.stream() instead
        val cursor: java.util.Iterator[String] = c
        val iterator: Iterator[String] = cursor.asScala
        fs2.Stream.fromBlockingIterator[IO](iterator, 512)
      }).map(fabric.parse.Json.parse)
    }
  }

  def collection(name: String): ArangoDBCollection = new ArangoDBCollection(db.collection(name))
}