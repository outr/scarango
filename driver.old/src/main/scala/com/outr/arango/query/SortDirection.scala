package com.outr.arango.query

sealed trait SortDirection

object SortDirection {
  case object ASC extends SortDirection
  case object DESC extends SortDirection
}