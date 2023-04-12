package com.outr.arango.query.dsl

import com.outr.arango.Ref
import com.outr.arango.query.{Query, QueryPart}

case class LetPartial(ref: Ref) {
  def :=(that: Query): Unit = {
    val context = QueryBuilderContext()
    context.addQuery(Query.merge(List(
      Query(List(
        QueryPart.Static("LET "),
        QueryPart.Ref(ref),
        QueryPart.Static(" = ")
      )),
      that
    ), ""))
  }
}