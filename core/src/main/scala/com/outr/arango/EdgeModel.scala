package com.outr.arango

trait EdgeModel[E <: Edge[E, From, To], From, To] extends DocumentModel[E] {
  val _from: Field[Id[From]] = field("_from")
  val _to: Field[Id[To]] = field("_to")

  override def `type`: CollectionType = CollectionType.Edge
}