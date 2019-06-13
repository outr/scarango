package com.outr.arango

import io.circe.{Decoder, Encoder, Json}

import scala.language.experimental.macros

case class Serialization[D](private val doc2Json: D => Json, private val json2Doc: Json => D) {
  final def toJson(document: D): Json = Id.update(doc2Json(document), removeIdentity = true)

  final def fromJson(json: Json): D = json2Doc(Id.update(json, removeIdentity = false))
}

object Serialization {
  def auto[D]: Serialization[D] = macro Macros.serializationAuto[D]
  def create[D](encoder: Encoder[D], decoder: Decoder[D]): Serialization[D] = {
    val doc2Json = (d: D) => encoder(d)
    val json2Doc = (json: Json) => decoder.decodeJson(json) match {
      case Left(df) => throw df
      case Right(d) => d
    }
    Serialization[D](doc2Json, json2Doc)
  }
}