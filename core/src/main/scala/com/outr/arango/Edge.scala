package com.outr.arango

trait Edge[E <: Document[E], Left <: Document[Left], Right <: Document[Right]] extends Document[E] {
  def _from: Id[Left]
  def _to: Id[Right]
}