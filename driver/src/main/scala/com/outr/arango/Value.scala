package com.outr.arango

sealed trait Value

case class StringValue(value: String) extends Value

case class IntValue(value: Int) extends Value

case class LongValue(value: Long) extends Value

case class DoubleValue(value: Double) extends Value

case class SeqStringValue(value: Seq[String]) extends Value

case class SeqIntValue(value: Seq[Int]) extends Value

case class SeqLongValue(value: Seq[Long]) extends Value

case class SeqDoubleValue(value: Seq[Double]) extends Value

object Value {
  def string(value: String): Value = StringValue(value)
  def int(value: Int): Value = IntValue(value)
  def long(value: Long): Value = LongValue(value)
  def double(value: Double): Value = DoubleValue(value)
  def strings(value: Seq[String]): Value = SeqStringValue(value)
  def ints(value: Seq[Int]): Value = SeqIntValue(value)
  def longs(value: Seq[Long]): Value = SeqLongValue(value)
  def doubles(value: Seq[Double]): Value = SeqDoubleValue(value)
  case object Null extends Value
}