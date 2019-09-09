package com.outr.arango

import io.circe.Json

import scala.language.implicitConversions

sealed trait Value

object Value {
  implicit def string(value: String): Value = if (value != null) StringValue(value) else Null
  implicit def string(value: Option[String]): Value = value.map(StringValue).getOrElse(Null)
  implicit def boolean(value: Boolean): Value = BooleanValue(value)
  implicit def boolean(value: Option[Boolean]): Value = value.map(BooleanValue).getOrElse(Null)
  implicit def int(value: Int): Value = IntValue(value)
  implicit def int(value: Option[Int]): Value = value.map(IntValue).getOrElse(Null)
  implicit def long(value: Long): Value = LongValue(value)
  implicit def long(value: Option[Long]): Value = value.map(LongValue).getOrElse(Null)
  implicit def double(value: Double): Value = DoubleValue(value)
  implicit def double(value: Option[Double]): Value = value.map(DoubleValue).getOrElse(Null)
  implicit def bigDecimal(value: BigDecimal): Value = BigDecimalValue(value)
  implicit def bigDecimal(value: Option[BigDecimal]): Value = value.map(BigDecimalValue).getOrElse(Null)
  implicit def strings(value: Seq[String]): Value = SeqStringValue(value)
  implicit def booleans(value: Seq[Boolean]): Value = SeqBooleanValue(value)
  implicit def ints(value: Seq[Int]): Value = SeqIntValue(value)
  implicit def longs(value: Seq[Long]): Value = SeqLongValue(value)
  implicit def doubles(value: Seq[Double]): Value = SeqDoubleValue(value)
  implicit def bigDecimals(value: Seq[BigDecimal]): Value = SeqBigDecimalValue(value)
  implicit def id[T](value: Id[T]): Value = StringValue(value._id)
  implicit def json(value: Json): Value = JsonValue(value)

  case class StringValue(value: String) extends Value
  case class BooleanValue(value: Boolean) extends Value
  case class IntValue(value: Int) extends Value
  case class LongValue(value: Long) extends Value
  case class DoubleValue(value: Double) extends Value
  case class BigDecimalValue(value: BigDecimal) extends Value
  case class SeqBooleanValue(value: Seq[Boolean]) extends Value
  case class SeqStringValue(value: Seq[String]) extends Value
  case class SeqIntValue(value: Seq[Int]) extends Value
  case class SeqLongValue(value: Seq[Long]) extends Value
  case class SeqDoubleValue(value: Seq[Double]) extends Value
  case class SeqBigDecimalValue(value: Seq[BigDecimal]) extends Value
  case class JsonValue(value: Json) extends Value
  case object Null extends Value
}