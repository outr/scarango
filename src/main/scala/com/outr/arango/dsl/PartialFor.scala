package com.outr.arango.dsl

class PartialFor(variableNames: List[String]) {
  def IN(expression: String): QueryBuilder = QueryBuilder(List(ForPart(variableNames, expression)))
}
