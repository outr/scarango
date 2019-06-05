package com.outr.arango

trait Document[D <: Document[D]] {
  def _identity: Id[D]

  final def _id: String = _identity._id
  final def _key: String = _identity._key
  final def _rev: Option[String] = _identity._rev
}