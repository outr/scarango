package com.outr.arango

/**
  * DocumentOption works similarly to Document except is also applicable for inserting as the values _key, _id, and _rev
  * can be excluded.
  */
trait DocumentOption {
  def _key: Option[String]
  def _id: Option[String]
  def _rev: Option[String]
}