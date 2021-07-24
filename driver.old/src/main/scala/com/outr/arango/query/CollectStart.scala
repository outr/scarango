package com.outr.arango.query

object CollectStart {
  def WITH(collectWith: CollectWith): CollectWith.Partial = new CollectWith.Partial(collectWith)
}