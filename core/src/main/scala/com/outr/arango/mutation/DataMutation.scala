package com.outr.arango.mutation

import fabric.Value

trait DataMutation {
  def store(value: Value): Value

  def retrieve(value: Value): Value
}