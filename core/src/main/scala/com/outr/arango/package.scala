package com.outr

import scala.concurrent.duration.FiniteDuration

package object arango {
  implicit class FieldList(fields: List[Field[_]]) {
    object index {
      def persistent(sparse: Boolean = false,
                     unique: Boolean = false): Index = {
        Index(IndexType.Persistent, fields.map(_.fieldName), sparse, unique)
      }

      def geo(geoJson: Boolean = true): Index = {
        Index(IndexType.Geo, fields.map(_.fieldName), geoJson = geoJson)
      }

      def fullText(minLength: Long = 3L): Index = {
        Index(IndexType.FullText, fields.map(_.fieldName), minLength = minLength)
      }

      def ttl(expireAfter: FiniteDuration): Index = {
        val seconds = expireAfter.toSeconds.toInt
        Index(IndexType.TTL, fields.map(_.fieldName), expireAfterSeconds = seconds)
      }
    }
  }
}