package com.outr.arango.json

import com.outr.arango.{DocumentModel, Id, Unique}
import fabric.define.DefType
import fabric.rw._
import fabric.{Json, obj}

import scala.language.implicitConversions

trait JsonDocumentModel extends DocumentModel[JsonDocument] {
  override implicit val rw: RW[JsonDocument] = RW.from[JsonDocument](
    r = jd => jd.json.merge(obj("_id" -> jd._id)),
    w = j => JsonDocument(j, j("_id").as[Id[JsonDocument]]),
    d = DefType.Obj(
      None,
      "json" -> DefType.Obj(None),
      "_id" -> Id.rw.definition
    )
  )

  implicit def json2JsonDocument(json: Json): JsonDocument = JsonDocument(json, _id = Id(Unique(), collectionName))
}