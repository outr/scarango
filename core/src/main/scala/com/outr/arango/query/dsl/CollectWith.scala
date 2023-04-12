package com.outr.arango.query.dsl

import com.outr.arango.Ref
import com.outr.arango.query.{Query, QueryPart}

sealed trait CollectWith {
  def value: String
}

object CollectWith {
  object Count extends CollectWith {
    override def value: String = "COUNT"
  }

  class Partial(collectWith: CollectWith) {
    def INTO(ref: Ref): Unit = {
      val context = QueryBuilderContext()
      val query = Query(List(
        QueryPart.Static(s"COLLECT WITH ${collectWith.value} INTO "),
        QueryPart.Ref(ref)
      ))
      context.addQuery(query)
    }
  }
}