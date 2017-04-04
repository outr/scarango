package com.outr.arango.dsl

import com.outr.arango.QueryArg

case class QueryBuilder(parts: List[QueryPart]) extends QueryPart {
  private def withParts(parts: QueryPart*): QueryBuilder = QueryBuilder(this.parts ::: parts.toList)

  def FILTER(conditions: Condition*): QueryBuilder = withParts(FilterPart(conditions.toList))
  def RETURN(expressions: String*): QueryBuilder = withParts(ReturnPart(expressions.toList))

  override lazy val aql: String = {
    QueryBuilder.counter.set(1)
    try {
      parts.map(_.aql).mkString(" ")
    } finally {
      QueryBuilder.counter.remove()
    }
  }
}

object QueryBuilder {
  private val counter = new ThreadLocal[Int]

  def nextArgName(): String = {
    val i = counter.get()
    counter.set(i + 1)
    s"arg$i"
  }
}

case class Condition(left: Expression, conditionType: ConditionType, right: Expression) extends QueryPart {
  override def aql: String = s"${left.aql} ${conditionType.aql} ${right.aql}"
}

sealed abstract class ConditionType(val aql: String) extends QueryPart

object ConditionType {
  case object === extends ConditionType("==")
}

case class FilterPart(conditions: List[Condition]) extends QueryPart {
  override lazy val aql: String = s"FILTER ${conditions.mkString(" && ")}"
}

sealed trait Expression extends QueryPart

case class StaticExpression(aql: String) extends QueryPart

case class ArgExpression(arg: QueryArg) extends QueryPart {
  override def aql: String = QueryBuilder.nextArgName()

  override def args: List[QueryArg] = List(arg)
}