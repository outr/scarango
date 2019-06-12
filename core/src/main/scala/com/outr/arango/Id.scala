package com.outr.arango

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

/**
  * Id represents the _key, _id, and _rev for a document
  *
  * @param value the unique identity
  * @param collection the collection name this id belongs to
  * @param revision the revision if retrieved from the database
  * @tparam D the document type
  */
case class Id[D](value: String,
                 collection: String,
                 revision: Option[String] = None) {
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

object Id {
  private val ExtractorRegex = """(.+)/(.+)""".r

  implicit def encoder[D]: Encoder[Id[D]] = new Encoder[Id[D]] {
    override def apply(id: Id[D]): Json = Json.fromString(id._id)
  }

  implicit def decoder[D]: Decoder[Id[D]] = new Decoder[Id[D]] {
    override def apply(c: HCursor): Result[Id[D]] = c.value.asString.get match {
      case ExtractorRegex(collection, value) => Right(Id[D](value, collection, None))
    }
  }
}