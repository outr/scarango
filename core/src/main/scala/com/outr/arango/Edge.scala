package com.outr.arango

/**
  * Edge represents a graph connection between two vertices
  *
  * @see https://www.arangodb.com/docs/stable/http/edge-working-with-edges.html#read-in--or-outbound-edges
  *
  * @tparam E the type of document for this edge
  * @tparam Left the left-side connection
  * @tparam Right the right-side connection
  */
trait Edge[E <: Document[E], Left, Right] extends Document[E] {
  def _from: Id[Left]
  def _to: Id[Right]
}