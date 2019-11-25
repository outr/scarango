package com.outr.arango

import scala.concurrent.duration.FiniteDuration

case class Field[F](fieldName: String) {
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

  lazy val opt: Field[Option[F]] = Field[Option[F]](fieldName)
}
