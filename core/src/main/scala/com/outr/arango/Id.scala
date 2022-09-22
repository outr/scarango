package com.outr.arango

import fabric._
import fabric.rw.{Asable, RW}

import scala.language.implicitConversions

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

  implicit def rw[D]: RW[Id[D]] = RW(_._id, v => parse[D](v.asStr.value))
  implicit def toJson[D](id: Id[D]): Json = rw[D].read(id)

  def parse[D](id: String): Id[D] = id match {
    case ExtractorRegex(collection, value) => Id[D](value, collection)
  }

  def extract[D](json: fabric.Json): Id[D] = update(json)("_id").as[Id[D]]

  def update(json: fabric.Json): fabric.Json = {
    val _key = json.get("_key").map(_.asStr.value)
    val _id = json.get("_id").map(_.asStr.value)
    val _identity = _id.map(parse[Any])

    if (_id.nonEmpty && _key.isEmpty) {
      json.merge(obj("_key" -> str(_identity.get.value)))
    } else {
      json
    }
  }
}