package com.outr

import scala.language.experimental.macros

package object arango {
  implicit class AQLInterpolator(val sc: StringContext) extends AnyVal {
    /**
      * AQL interpolation with compile-time validation against ArangoDB
      */
    def aql(args: Any*): Query = macro AQLMacros.aql

    /**
      * AQL interpolation unvalidated.
      *
      * WARNING: This can lead to runtime errors if you use invalid AQL. Ideally, this should only be used for partial
      * queries.
      */
    def aqlu(args: Any*): Query = macro AQLMacros.aqlu
  }
}