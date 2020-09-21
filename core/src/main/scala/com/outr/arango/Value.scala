package com.outr.arango

import io.circe.Json

import scala.language.implicitConversions

case class Value(json: Json, static: Boolean = false, excludeAt: Boolean = false)

object Value {
  implicit def string(value: String): Value = if (value != null) Value(Json.fromString(value)) else Value(Json.Null)
  implicit def string(value: Option[String]): Value = Value(value.map(Json.fromString).getOrElse(Json.Null))
  implicit def boolean(value: Boolean): Value = Value(Json.fromBoolean(value))
  implicit def boolean(value: Option[Boolean]): Value = Value(value.map(Json.fromBoolean).getOrElse(Json.Null))
  implicit def int(value: Int): Value = Value(Json.fromInt(value))
  implicit def int(value: Option[Int]): Value = Value(value.map(Json.fromInt).getOrElse(Json.Null))
  implicit def long(value: Long): Value = Value(Json.fromLong(value))
  implicit def long(value: Option[Long]): Value = Value(value.map(Json.fromLong).getOrElse(Json.Null))
  implicit def double(value: Double): Value = Value(Json.fromDouble(value).get)
  implicit def double(value: Option[Double]): Value = Value(value.map(Json.fromDouble(_).get).getOrElse(Json.Null))
  implicit def bigDecimal(value: BigDecimal): Value = Value(Json.fromBigDecimal(value))
  implicit def bigDecimal(value: Option[BigDecimal]): Value = Value(value.map(Json.fromBigDecimal).getOrElse(Json.Null))
  implicit def values(values: Seq[Value]): Value = Value(Json.arr(values.map(_.json): _*))
  implicit def strings(value: Seq[String]): Value = conv[String](value, string)
  implicit def booleans(value: Seq[Boolean]): Value = conv[Boolean](value, boolean)
  implicit def ints(value: Seq[Int]): Value = conv[Int](value, int)
  implicit def longs(value: Seq[Long]): Value = conv[Long](value, long)
  implicit def doubles(value: Seq[Double]): Value = conv[Double](value, double)
  implicit def bigDecimals(value: Seq[BigDecimal]): Value = conv[BigDecimal](value, bigDecimal)
  implicit def id[T](value: Id[T]): Value = string(value._id)
  implicit def json(value: Json): Value = Value(value)

  def static(value: String, excludeAt: Boolean = false): Value = if (value != null) {
    Value(Json.fromString(value), static = true, excludeAt = excludeAt)
  } else {
    Value(Json.Null, static = true, excludeAt = excludeAt)
  }

  private def conv[T](seq: Seq[T], converter: T => Value): Value = {
    val values = seq.toList.map(converter).map(_.json)
    Value(Json.arr(values: _*))
  }
}