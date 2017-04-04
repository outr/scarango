package com.outr.arango.dsl

case class ForPart(variableNames: List[String], expression: String) extends QueryPart {
  override def aql: String = s"FOR ${variableNames.mkString(", ")} IN $expression"
}
