package com.outr.arango.query.dsl

import com.outr.arango.query.Query
import com.outr.arango.Ref

case class LetPartial(ref: Ref) {
  def :=(that: Query): Unit = {
    val context = QueryBuilderContext()
    val name = context.name(ref)
    context.addQuery(Query.merge(List(Query(s"LET $name = "), that), ""))
  }
}