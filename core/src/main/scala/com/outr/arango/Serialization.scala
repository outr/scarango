package com.outr.arango

import io.circe.Json

trait Serialization[D <: Document[D]] {
  def toJson(document: D): Json
  def fromJson(json: Json): D
}