package com.outr.arango.aql

object CollectStart {
  def WITH(collectWith: CollectWith): CollectWith.Partial = new CollectWith.Partial(collectWith)
}