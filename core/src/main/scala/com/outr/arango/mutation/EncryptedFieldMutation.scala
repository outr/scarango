package com.outr.arango.mutation

import com.outr.arango.Field
import com.outr.scalapass.{CryptoInstance, Encrypted}
import fabric.{Value, obj}
import fabric.parse.{Json, JsonWriter}
import fabric.rw._

case class EncryptedFieldMutation(crypto: Field[_] => CryptoInstance, fields: Field[_]*) extends DataMutation {
  override def store(value: Value): Value = fields.foldLeft(value)((v, f) =>
    value.get(f.name) match {
      case Some(fieldValue) =>
        val instance = crypto(f)
        val jsonString = Json.format(fieldValue, JsonWriter.Compact)
        val encrypted = instance.encrypt(jsonString)
        val json = encrypted.toValue
        value.merge(obj(
          f.name -> json
        ))
      case None => v
    }
  )

  override def retrieve(value: Value): Value = fields.foldLeft(value)((v, f) =>
    value.get(f.name) match {
      case Some(fieldValue) =>
        val instance = crypto(f)
        val encrypted = fieldValue.as[Encrypted]
        val jsonString = instance.decrypt(encrypted)
        val json = Json.parse(jsonString)
        value.merge(obj(
          f.name -> json
        ))
      case None => v
    }
  )
}