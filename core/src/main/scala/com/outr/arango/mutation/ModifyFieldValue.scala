package com.outr.arango.mutation

import com.outr.arango.Field
import fabric._
import fabric.rw._

case class ModifyFieldValue[F](field: Field[F],
                               storage: F => F,
                               retrieval: F => F)
                              (implicit rw: RW[F]) extends FieldMutation[F] {
  override def store(value: Json, fieldValue: Option[Json]): Json = fieldValue match {
    case Some(v) =>
      val f = storage(v.as[F])
      value.merge(obj(
        field.fieldName -> f.json
      ))
    case None => value
  }

  override def retrieve(value: Json, fieldValue: Option[Json]): Json = fieldValue match {
    case Some(v) =>
      val f = retrieval(v.as[F])
      value.merge(obj(
        field.fieldName -> f.json
      ))
    case None => value
  }
}
