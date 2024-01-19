package com.outr.arango.query

import fabric.rw.RW

import scala.language.implicitConversions

sealed trait SortDirection

object SortDirection {
  implicit def toQueryPart(sort: SortDirection): QueryPart = QueryPart.Static(sort.toString)
  implicit val rw: RW[SortDirection] = RW.enumeration(List(ASC, DESC), asString = _.toString)

  case object ASC extends SortDirection {
    override def toString: String = "ASC"
  }
  case object DESC extends SortDirection {
    override def toString: String = "DESC"
  }
}