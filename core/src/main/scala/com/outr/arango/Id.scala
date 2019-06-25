package com.outr.arango

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}
import com.outr.arango.JsonImplicits._

/**
  * Id represents the _key, _id, and _rev for a document
  *
  * @param value the unique identity
  * @param collection the collection name this id belongs to
  * @tparam D the document type
  */
case class Id[D](value: String,
                 collection: String) {
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

  override def toString: String = _id
}

object Id {
  private val ExtractorRegex = """(.+)/(.+)""".r

  implicit def encoder[D]: Encoder[Id[D]] = new Encoder[Id[D]] {
    override def apply(id: Id[D]): Json = Json.fromString(id._id)
  }

  implicit def decoder[D]: Decoder[Id[D]] = new Decoder[Id[D]] {
    override def apply(c: HCursor): Result[Id[D]] = c.value.asString.get match {
      case ExtractorRegex(collection, value) => Right(Id[D](value, collection))
    }
  }

  def parse[D](id: String): Id[D] = id match {
    case ExtractorRegex(collection, value) => Id[D](value, collection)
  }

  def extract[D](json: Json): Id[D] = {
    val updated = update(json)
    decoder[D].decodeJson((updated \ "_id").get) match {
      case Left(df) => throw df
      case Right(id) => id
    }
  }

  def update(json: Json): Json = {
    val _key = (json \ "_key").flatMap(_.asString)
    val _id = (json \ "_id").flatMap(_.asString)
    val _identity = _id.map(parse[Any])

    if (_id.nonEmpty && _key.isEmpty) {
      json.deepMerge(Json.obj("_key" -> Json.fromString(_identity.get.value)))
    } else {
      json
    }
  }
}