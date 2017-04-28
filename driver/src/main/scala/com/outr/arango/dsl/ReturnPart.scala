package com.outr.arango.dsl

case class ReturnPart(expressions: List[String]) extends QueryPart {
  override def aql: String = if (expressions.tail.isEmpty) {
    s"RETURN ${expressions.head}"
  } else {
    s"RETURN ${expressions.mkString("{", ", ", "}")}"
  }
}