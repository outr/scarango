package com.outr.arango

import fabric.{Value, obj}

object IdMutation extends DataMutation {
  override def store(value: Value): Value = value.get("_id") match {
    case Some(v) =>
      val s = v.asString
      val index = s.indexOf('/')
      val _key = s.substring(index + 1)
      value.merge(obj(
        "_key" -> _key
      ))
  }

  override def retrieve(value: Value): Value = value
}