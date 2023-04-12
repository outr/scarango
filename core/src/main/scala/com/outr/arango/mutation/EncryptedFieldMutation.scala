package com.outr.arango.mutation

import com.outr.arango.Field
import com.outr.scalapass.{CryptoInstance, Encrypted}
import fabric.io.{Format, JsonFormatter, JsonParser}
import fabric.rw._
import fabric.{Json, obj}

case class EncryptedFieldMutation(crypto: Field[_] => CryptoInstance, fields: Field[_]*) extends DataMutation {
  override def store(value: Json): Json = fields.foldLeft(value)((v, f) =>
    value.get(f.fieldName) match {
      case Some(fieldValue) =>
        val instance = crypto(f)
        val jsonString = JsonFormatter.Compact(fieldValue)
        val encrypted = instance.encrypt(jsonString)
        val json = encrypted.json
        value.merge(obj(
          f.fieldName -> json
        ))
      case None => v
    }
  )

  override def retrieve(value: Json): Json = fields.foldLeft(value)((v, f) =>
    value.get(f.fieldName) match {
      case Some(fieldValue) =>
        val instance = crypto(f)
        val encrypted = fieldValue.as[Encrypted]
        val jsonString = instance.decrypt(encrypted)
        val json = JsonParser(jsonString, Format.Json)
        value.merge(obj(
          f.fieldName -> json
        ))
      case None => v
    }
  )
}