package com.outr.arango

/**
  * DocumentOption works similarly to Document except is also applicable for inserting as the values _key, _id, and _rev
  * can be excluded.
  */
trait DocumentOption {
  /**
    * Key represents the unique identifier within a collection.
    *
    * @see https://docs.arangodb.com/3.1/HTTP/Document/AddressAndEtag.html#document-key
    */
  def _key: Option[String]

  /**
    * Id represents the concatenation of collection name and key.
    *
    * For example: "myusers/3456789"
    *
    * @see https://docs.arangodb.com/3.1/HTTP/Document/AddressAndEtag.html#document-handle
    */
  def _id: Option[String]

  /**
    * Revision represents a unique identifier representing the current status of this document. Each time a document is
    * changed this value will be re-defined.
    *
    * @see https://docs.arangodb.com/3.1/HTTP/Document/AddressAndEtag.html#document-revision
    */
  def _rev: Option[String]
}