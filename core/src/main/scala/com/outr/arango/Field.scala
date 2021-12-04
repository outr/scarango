package com.outr.arango

import com.outr.arango.query.{QueryPart, toValue}

import scala.concurrent.duration.FiniteDuration

case class Field[F](fieldName: String) extends QueryPart.Support {
  protected def field[T](name: String): Field[T] = Field(s"$fieldName.$name")

  object index {
    def persistent(sparse: Boolean = false,
                  unique: Boolean = false): Index = {
      Index(IndexType.Persistent, List(fieldName), sparse, unique)
    }
    def geo(geoJson: Boolean = true): Index = {
      Index(IndexType.Geo, List(fieldName), geoJson = geoJson)
    }
    def fullText(minLength: Long = 3L): Index = {
      Index(IndexType.FullText, List(fieldName), minLength = minLength)
    }
    def ttl(expireAfter: FiniteDuration): Index = {
      val seconds = expireAfter.toSeconds.toInt
      Index(IndexType.TTL, List(fieldName), expireAfterSeconds = seconds)
    }
  }

  def apply(value: F): FieldAndValue[F] = FieldAndValue(this, toValue(value))

  lazy val opt: Field[Option[F]] = Field[Option[F]](fieldName)

  override def toQueryPart: QueryPart = QueryPart.Static(fieldName)
}
