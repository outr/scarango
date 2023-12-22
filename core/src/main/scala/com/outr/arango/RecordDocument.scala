package com.outr.arango

/**
  * Represents a document with a `created` and `modified`. Coupled with `RecordDocumentModel` defines a computed value
  * to update `modified` then the record changes in the database.
  */
trait RecordDocument[D <: RecordDocument[D]] extends Document[D] {
  def created: Long
  def modified: Long
}