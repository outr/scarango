package com.outr.arango

case class Query(value: String, args: Map[String, QueryArg])

sealed trait QueryArg

object QueryArg {
  case class string(value: String) extends QueryArg
  case class int(value: Int) extends QueryArg
  case class double(value: Double) extends QueryArg
}