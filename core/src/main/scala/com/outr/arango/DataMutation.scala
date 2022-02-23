package com.outr.arango

import fabric.Value

trait DataMutation {
  def store(value: Value): Value

  def retrieve(value: Value): Value
}