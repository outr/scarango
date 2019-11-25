package com.outr

import scala.language.implicitConversions

package object arango {
  implicit def field2String[T](field: Field[T]): String = field.fieldName
  implicit def fieldList2Strings[T](fields: List[Field[T]]): List[String] = fields.map(_.fieldName)
}