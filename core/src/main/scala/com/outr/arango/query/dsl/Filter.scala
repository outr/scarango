package com.outr.arango.query.dsl

import com.outr.arango.query._

class Filter(left: Query, condition: String, right: Query) {
  def &&(filter: Filter): Filter = {
    new Filter(
      left = build().withPrefixParts(Filter.WrapOpen),
      condition = "&&",
      right = filter.build().withParts(Filter.WrapClose)
    )
  }
  def ||(filter: Filter): Filter = {
    new Filter(
      left = build().withPrefixParts(Filter.WrapOpen),
      condition = "||",
      right = filter.build().withParts(Filter.WrapClose)
    )
  }

  def build(): Query = Query.merge(List(
    left,
    Query(List(QueryPart.Static(condition))),
    right
  ), separator = " ")
}

object Filter {
  private val WrapOpen: QueryPart = QueryPart.Static("(")
  private val WrapClose: QueryPart = QueryPart.Static(")")
}