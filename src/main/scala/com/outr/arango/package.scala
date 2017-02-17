package com.outr

import scala.language.experimental.macros

package object arango {
  implicit class AQLInterpolator(val sc: StringContext) extends AnyVal {
    def aql(args: Any*): Query = macro Macros.aql
  }
}

case class Query(value: String, args: List[QueryArg])

sealed trait QueryArg