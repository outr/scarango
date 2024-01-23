package com.outr

import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

package object arango {
  implicit class FieldList(fields: List[Field[_]]) {
    object index {
      def persistent(sparse: Boolean = false,
                     unique: Boolean = false): Index = Index.Persistent(
        fields = fields.map(_.fieldName),
        sparse = sparse,
        unique = unique
      )

      def geo(geoJson: Boolean = true): Index = Index.Geo(
        fields = fields.map(_.fieldName),
        geoJson = geoJson
      )

      def ttl(expireAfter: FiniteDuration): Index = Index.TTL(
        fields = fields.map(_.fieldName),
        expireAfterSeconds = expireAfter.toSeconds.toInt
      )
    }
  }
}