package com.outr.arango

trait Document[D <: Document[D]] {
  def _id: Id[D]
}