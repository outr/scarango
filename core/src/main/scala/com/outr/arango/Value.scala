package com.outr.arango

import fabric._

import scala.language.implicitConversions

case class Value(json: fabric.Value, static: Boolean = false, excludeAt: Boolean = false)

object Value {
  implicit def string(value: String): Value = if (value != null) Value(str(value)) else Value(Null)
  implicit def string(value: Option[String]): Value = Value(value.map(str).getOrElse(Null))
  implicit def boolean(value: Boolean): Value = Value(bool(value))
  implicit def boolean(value: Option[Boolean]): Value = Value(value.map(bool).getOrElse(Null))
  implicit def int(value: Int): Value = Value(num(value))
  implicit def int(value: Option[Int]): Value = Value(value.map(i => num(i)).getOrElse(Null))
  implicit def long(value: Long): Value = Value(num(value.toDouble))
  implicit def long(value: Option[Long]): Value = Value(value.map(l => num(l.toDouble)).getOrElse(Null))
  implicit def double(value: Double): Value = Value(num(value))
  implicit def double(value: Option[Double]): Value = Value(value.map(num).getOrElse(Null))
  implicit def bigDecimal(value: BigDecimal): Value = Value(num(value))
  implicit def bigDecimal(value: Option[BigDecimal]): Value = Value(value.map(num).getOrElse(Null))
  implicit def values(values: Seq[Value]): Value = Value(arr(values.map(_.json): _*))
  implicit def strings(value: Seq[String]): Value = conv[String](value, string)
  implicit def booleans(value: Seq[Boolean]): Value = conv[Boolean](value, boolean)
  implicit def ints(value: Seq[Int]): Value = conv[Int](value, int)
  implicit def longs(value: Seq[Long]): Value = conv[Long](value, long)
  implicit def doubles(value: Seq[Double]): Value = conv[Double](value, double)
  implicit def bigDecimals(value: Seq[BigDecimal]): Value = conv[BigDecimal](value, bigDecimal)
  implicit def id[T](value: Id[T]): Value = string(value._id)
  implicit def json(value: fabric.Value): Value = Value(value)

  def static(value: String, excludeAt: Boolean = false): Value = if (value != null) {
    Value(str(value), static = true, excludeAt = excludeAt)
  } else {
    Value(Null, static = true, excludeAt = excludeAt)
  }

  private def conv[T](seq: Seq[T], converter: T => Value): Value = {
    val values = seq.toList.map(converter).map(_.json)
    Value(arr(values: _*))
  }
}