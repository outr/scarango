package com.outr.arango.mutation

import com.outr.arango.Field
import fabric.Json

trait FieldMutation[F] extends DataMutation {
  def field: Field[F]

  override final def store(value: Json): Json = {
    val v = value.get(field.fieldName)
    store(value, v)
  }

  override final def retrieve(value: Json): Json = {
    val v = value.get(field.fieldName)
    retrieve(value, v)
  }

  def store(value: Json, fieldValue: Option[Json]): Json

  def retrieve(value: Json, fieldValue: Option[Json]): Json
}