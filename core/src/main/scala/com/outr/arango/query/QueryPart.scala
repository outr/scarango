package com.outr.arango.query

sealed trait QueryPart

object QueryPart {
  case class Container(parts: List[QueryPart]) extends QueryPart
  case class Static(value: String) extends QueryPart
  case class Variable(value: fabric.Value) extends QueryPart
  case class NamedVariable(name: String, value: fabric.Value) extends QueryPart
  trait Support extends QueryPart {
    def toQueryPart: QueryPart
  }
}