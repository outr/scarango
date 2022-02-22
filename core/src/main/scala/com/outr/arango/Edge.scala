package com.outr.arango

/**
  * Edge represents a graph connection between two vertices
  *
  * @see https://www.arangodb.com/docs/stable/http/edge-working-with-edges.html#read-in--or-outbound-edges
  *
  * @tparam E the type of document for this edge
  * @tparam From the left-side connection
  * @tparam To the right-side connection
  */
trait Edge[E <: Document[E], From, To] extends Document[E] {
  def _from: Id[From]
  def _to: Id[To]
}