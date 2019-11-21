package com.outr.arango.aql

import com.outr.arango.{Query, Ref}

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
        value = s"COLLECT WITH ${collectWith.value} INTO ${context.name(ref)}",
        args = Map.empty
      )
      context.addQuery(query)
    }
  }
}