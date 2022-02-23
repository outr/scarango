package com.outr.arango.mutation

import com.outr.arango.Field
import fabric.rw._
import fabric._

case class ModifyFieldValue[F](field: Field[F],
                               storage: F => F,
                               retrieval: F => F)
                              (implicit rw: ReaderWriter[F]) extends FieldMutation[F] {
  override def store(value: Value, fieldValue: Option[Value]): Value = fieldValue match {
    case Some(v) =>
      val f = storage(v.as[F])
      value.merge(obj(
        field.name -> f.toValue
      ))
    case None => value
  }

  override def retrieve(value: Value, fieldValue: Option[Value]): Value = fieldValue match {
    case Some(v) =>
      val f = retrieval(v.as[F])
      value.merge(obj(
        field.name -> f.toValue
      ))
    case None => value
  }
}
