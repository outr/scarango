package com.outr.arango

import fabric.rw.ReaderWriter

/**
  * Id represents the _key, _id, and _rev for a document
  *
  * @param value the unique identity
  * @param collection the collection name this id belongs to
  * @tparam D the document type
  */
case class Id[D](value: String,
                 collection: String) extends Ordered[Id[D]] {
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

  override def compare(that: Id[D]): Int = this._id.compare(that._id)

  override def toString: String = _id
}

object Id {
  private val ExtractorRegex = """(.+)/(.+)""".r

  implicit def rw[D]: ReaderWriter[Id[D]] = ReaderWriter(_._id, v => parse[D](v.asStr.value))

  def parse[D](id: String): Id[D] = id match {
    case ExtractorRegex(collection, value) => Id[D](value, collection)
  }
}