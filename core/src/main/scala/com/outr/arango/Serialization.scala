package com.outr.arango

import io.circe.Json

import scala.language.experimental.macros

case class Serialization[D <: Document[D]](doc2Json: D => Json, json2Doc: Json => D) {
  final def toJson(document: D): Json = {
    val json = doc2Json(document)
    val identity = document._identity
    json.deepMerge(Json.obj(
      "_id" -> Json.fromString(identity._id),
      "_key" -> Json.fromString(identity._key),
      "_rev" -> identity._rev.map(Json.fromString).getOrElse(Json.Null)
    ))
  }

  final def fromJson(json: Json): D = {
    val _id = (json \\ "_id").head.asString.get
    val _key = (json \\ "_key").head.asString.get
    val _rev = (json \\ "_rev").head.asString.get
    json2Doc(json.deepMerge(Json.obj(
      "_identity" -> Json.obj(
        "_id" -> Json.fromString(_id),
        "_key" -> Json.fromString(_key),
        "_rev" -> Json.fromString(_rev)
      )
    )))
  }
}

object Serialization {
  def auto[D <: Document[D]]: Serialization[D] = macro Macros.serializationAuto[D]
}