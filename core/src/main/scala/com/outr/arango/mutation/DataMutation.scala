package com.outr.arango.mutation

import fabric.Json

trait DataMutation {
  def store(value: Json): Json

  def retrieve(value: Json): Json
}