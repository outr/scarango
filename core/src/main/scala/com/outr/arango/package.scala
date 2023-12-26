package com.outr

import com.outr.arango.collection.DocumentCollection

import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

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

      def ttl(expireAfter: FiniteDuration): Index = {
        val seconds = expireAfter.toSeconds.toInt
        Index(IndexType.TTL, fields.map(_.fieldName), expireAfterSeconds = seconds)
      }
    }
  }
}