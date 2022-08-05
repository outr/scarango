package com.outr.arango.mutation

import fabric._

object IdMutation extends DataMutation {
  override def store(value: Json): Json = value.get("_id") match {
    case Some(v) =>
      val s = v.asString
      val index = s.indexOf('/')
      val _key = s.substring(index + 1)
      value.merge(obj(
        "_key" -> _key
      ))
    case None => value
  }

  override def retrieve(value: Json): Json = value
}