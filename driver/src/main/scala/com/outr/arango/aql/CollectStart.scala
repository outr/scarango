package com.outr.arango.aql

import com.outr.arango.aql

object CollectStart {
  def WITH(collectWith: CollectWith): CollectWith.Partial = new aql.CollectWith.Partial(collectWith)
}