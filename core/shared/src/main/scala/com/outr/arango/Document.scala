package com.outr.arango

/**
  * Document represents a result coming back from the database and will always have _key, _id, and _rev.
  */
trait Document {
  def _key: String
  def _id: String
  def _rev: String
}