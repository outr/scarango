package com.outr.arango

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}
import com.outr.arango.JsonImplicits._

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

  override def toString: String = revision match {
    case Some(rev) => s"$rev@${_id}"
    case None => _id
  }
}

object Id {
  private val ExtractorRegex = """(.+)/(.+)""".r
  private val ExtractorWithRevisionRegex = """(.+)[@](.+)/(.+)""".r

  implicit def encoder[D]: Encoder[Id[D]] = new Encoder[Id[D]] {
    override def apply(id: Id[D]): Json = Json.fromString(id._id)
  }

  implicit def decoder[D]: Decoder[Id[D]] = new Decoder[Id[D]] {
    override def apply(c: HCursor): Result[Id[D]] = c.value.asString.get match {
      case ExtractorWithRevisionRegex(rev, collection, value) => Right(Id[D](value, collection, Some(rev)))
      case ExtractorRegex(collection, value) => Right(Id[D](value, collection, None))
    }
  }

  def parse[D](id: String, rev: Option[String] = None): Id[D] = id match {
    case ExtractorRegex(collection, value) => Id[D](value, collection, rev)
  }

  def extract[D](json: Json): Id[D] = {
    val updated = update(json, removeIdentity = false)
    decoder[D].decodeJson((updated \ "_identity").get) match {
      case Left(df) => throw df
      case Right(id) => id
    }
  }

  def update(json: Json, removeIdentity: Boolean): Json = {
    val _identity = (json \ "_identity").map(decoder[Any].decodeJson).map {
      case Left(df) => throw df
      case Right(id) => id
    }
    val _key = (json \ "_key").flatMap(_.asString)
    val _id = (json \ "_id").flatMap(_.asString)
    val _rev = (json \ "_rev").flatMap(_.asString)
    val updated = if (_identity.isEmpty && _id.nonEmpty) {
      val id = parse[Any](_id.get, _rev)
      json.deepMerge(Json.obj("_identity" -> Json.fromString(id.toString)))
    } else if (_identity.nonEmpty && (_key.isEmpty || _id.isEmpty)) {
      val id = _identity.get
      json.deepMerge(Json.obj(
        "_key" -> Json.fromString(id._key),
        "_id" -> Json.fromString(id._id),
        "_rev" -> id._rev.map(Json.fromString).getOrElse(Json.Null)
      ))
    } else {
      json
    }
    if (removeIdentity) {
      updated.asObject.map(_.filter(_._1 != "_identity")).map(Json.fromJsonObject).getOrElse(updated)
    } else {
      updated
    }
  }
}