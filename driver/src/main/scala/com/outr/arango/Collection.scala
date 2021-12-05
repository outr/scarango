package com.outr.arango

import com.outr.arango.query.QueryPart

trait Collection extends QueryPart.Support {
  def `type`: CollectionType
  def dbName: String
  def name: String

  override def toQueryPart: QueryPart = QueryPart.Static(name)
}