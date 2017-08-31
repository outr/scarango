package com.outr.arango

sealed trait Value

case class StringValue(value: String) extends Value

case class BooleanValue(value: Boolean) extends Value

case class IntValue(value: Int) extends Value

case class LongValue(value: Long) extends Value

case class DoubleValue(value: Double) extends Value

case class SeqBooleanValue(value: Seq[Boolean]) extends Value

case class SeqStringValue(value: Seq[String]) extends Value

case class SeqIntValue(value: Seq[Int]) extends Value

case class SeqLongValue(value: Seq[Long]) extends Value

case class SeqDoubleValue(value: Seq[Double]) extends Value

object Value {
  def string(value: String): Value = if (value != null) StringValue(value) else Null
  def string(value: Option[String]): Value = value.map(StringValue).getOrElse(Null)
  def boolean(value: Boolean): Value = BooleanValue(value)
  def boolean(value: Option[Boolean]): Value = value.map(BooleanValue).getOrElse(Null)
  def int(value: Int): Value = IntValue(value)
  def int(value: Option[Int]): Value = value.map(IntValue).getOrElse(Null)
  def long(value: Long): Value = LongValue(value)
  def long(value: Option[Long]): Value = value.map(LongValue).getOrElse(Null)
  def double(value: Double): Value = DoubleValue(value)
  def double(value: Option[Double]): Value = value.map(DoubleValue).getOrElse(Null)
  def strings(value: Seq[String]): Value = SeqStringValue(value)
  def booleans(value: Seq[Boolean]): Value = SeqBooleanValue(value)
  def ints(value: Seq[Int]): Value = SeqIntValue(value)
  def longs(value: Seq[Long]): Value = SeqLongValue(value)
  def doubles(value: Seq[Double]): Value = SeqDoubleValue(value)
  case object Null extends Value
}