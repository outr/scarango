package com.outr.arango

trait Document[D <: Document[D]] {
  def _identity: Id[D]
}