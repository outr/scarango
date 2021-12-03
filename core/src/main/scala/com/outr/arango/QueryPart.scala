package com.outr.arango

sealed trait QueryPart

object QueryPart {
  case class Static(value: String) extends QueryPart
  case class Variable(value: fabric.Value) extends QueryPart
  case class NamedVariable(name: String, value: fabric.Value) extends QueryPart
  trait Support extends QueryPart {
    def toQueryPart: QueryPart
  }
}