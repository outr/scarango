package com.outr.arango.query

sealed trait QueryPart

object QueryPart {
  case class Container(parts: List[QueryPart]) extends QueryPart
  case class Static(value: String) extends QueryPart
  case class Variable(value: fabric.Json) extends QueryPart
  case class NamedVariable(name: String, value: fabric.Json) extends QueryPart
  trait Support extends QueryPart {
    def toQueryPart: QueryPart
  }
}