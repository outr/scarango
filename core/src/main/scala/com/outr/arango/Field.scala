package com.outr.arango

import scala.concurrent.duration.FiniteDuration

case class Field[F](name: String) {
  object index {
    def hash(sparse: Boolean = false,
             unique: Boolean = false,
             deduplicate: Boolean = true): Index = {
      Index(IndexType.Hash, List(name), sparse, unique, deduplicate)
    }
    def skipList(sparse: Boolean = false,
                 unique: Boolean = false,
                 deduplicate: Boolean = true): Index = {
      Index(IndexType.SkipList, List(name), sparse, unique, deduplicate)
    }
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

  lazy val opt: Field[Option[F]] = Field[Option[F]](name)
}
