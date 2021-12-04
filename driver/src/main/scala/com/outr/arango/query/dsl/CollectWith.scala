package com.outr.arango.query.dsl

import com.outr.arango.Ref
import com.outr.arango.query.Query

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
      val query = Query(
        s"COLLECT WITH ${collectWith.value} INTO ${context.name(ref)}"
      )
      context.addQuery(query)
    }
  }
}