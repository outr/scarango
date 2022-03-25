package com.outr.arango

import com.outr.arango.mutation.DataMutation
import com.outr.arango.query.QueryPart
import fabric.rw._

import scala.concurrent.duration.FiniteDuration

class Field[F: ReaderWriter] private(val name: String, val mutation: Option[DataMutation]) extends QueryPart.Support {
  object index {
    def persistent(sparse: Boolean = false,
                  unique: Boolean = false): Index = {
      Index(IndexType.Persistent, List(name), sparse, unique)
    }
    def geo(geoJson: Boolean = true): Index = {
      Index(IndexType.Geo, List(name), geoJson = geoJson)
    }
    def fullText(minLength: Long = 3L): Index = {
      Index(IndexType.FullText, List(name), minLength = minLength)
    }
    def ttl(expireAfter: FiniteDuration): Index = {
      val seconds = expireAfter.toSeconds.toInt
      Index(IndexType.TTL, List(name), expireAfterSeconds = seconds)
    }
  }

  def apply(value: F): FieldAndValue[F] = FieldAndValue(this, value.toValue)

  lazy val opt: Field[Option[F]] = Field[Option[F]](name)

  override def toQueryPart: QueryPart = QueryPart.Static(name)
}

object Field {
  def apply[F: ReaderWriter](name: String): Field[F] = new Field[F](name, None)
  def apply[F: ReaderWriter](name: String, mutation: DataMutation): Field[F] = new Field[F](name, Some(mutation))
  def apply[F: ReaderWriter](name: String, mutation: Option[DataMutation]): Field[F] = new Field[F](name, mutation)
}