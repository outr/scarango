package com.outr.arango

/**
  * Id represents the _key, _id, and _rev for a document
  *
  * @param value the unique identity
  * @param collection the collection name this id belongs to
  * @param revision the revision if retrieved from the database
  * @tparam D the document type
  */
case class Id[D <: Document[D]](value: String,
                                collection: String,
                                revision: Option[String]) {
  /**
    * Key represents the unique identifier within a collection.
    *
    * @see https://www.arangodb.com/docs/stable/http/document-address-and-etag.html#document-key
    */
  lazy val _key: String = value

  /**
    * Id represents the concatenation of collection name and key.
    *
    * For example: "myusers/3456789"
    *
    * @see https://www.arangodb.com/docs/stable/http/document-address-and-etag.html#document-handle
    */
  lazy val _id: String = s"$collection/$value"

  /**
    * Revision represents a unique identifier representing the current status of this document. Each time a document is
    * changed this value will be re-defined.
    *
    * @see https://www.arangodb.com/docs/stable/http/document-address-and-etag.html#document-revision
    */
  def _rev: Option[String] = revision
}