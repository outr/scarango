package com.outr.arango.mutation

import com.outr.arango.Field
import fabric.Value

trait FieldMutation[F] extends DataMutation {
  def field: Field[F]

  override final def store(value: Value): Value = {
    val v = value.get(field.name)
    store(value, v)
  }

  override final def retrieve(value: Value): Value = {
    val v = value.get(field.name)
    retrieve(value, v)
  }

  def store(value: Value, fieldValue: Option[Value]): Value

  def retrieve(value: Value, fieldValue: Option[Value]): Value
}