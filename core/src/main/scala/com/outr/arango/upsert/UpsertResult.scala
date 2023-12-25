package com.outr.arango.upsert

import fabric.rw._

case class UpsertResult[D](original: Option[D], newValue: D)

object UpsertResult {
  implicit def rw[D: RW]: RW[UpsertResult[D]] = RW.gen
}