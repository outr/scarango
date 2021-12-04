package com.outr.arango.query.dsl

object CollectStart {
  def WITH(collectWith: CollectWith): CollectWith.Partial = new CollectWith.Partial(collectWith)
}
