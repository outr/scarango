package com.outr.arango

sealed trait Value

case class StringValue(value: String) extends Value

case class IntValue(value: Int) extends Value

case class DoubleValue(value: Double) extends Value

object Value {
  def apply(value: String): Value = StringValue(value)
  def apply(value: Int): Value = IntValue(value)
  def apply(value: Double): Value = DoubleValue(value)
}