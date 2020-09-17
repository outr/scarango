package com.outr.arango.monitored

import com.outr.arango.{Collection, Document, NamedRef, Query, WritableCollection}

trait MaterializedPart[Base <: Document[Base], Into <: Document[Into]] {
  def updateQueryPart(baseRef: NamedRef, ids: NamedRef, updatedRef: NamedRef): Option[Query]

  def references(base: Collection[Base], into: WritableCollection[Into]): List[Reference[_ <: Document[_]]]
}